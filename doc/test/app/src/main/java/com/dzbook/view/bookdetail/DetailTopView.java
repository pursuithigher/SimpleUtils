package com.dzbook.view.bookdetail;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dzbook.activity.vip.MyVipActivity;
import com.dzbook.event.EventBusUtils;
import com.dzbook.imageloader.AnimMode;
import com.dzbook.imageloader.DataManager;
import com.dzbook.imageloader.ImageConfig;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.MathUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.comment.CommentRatingBarView;
import com.huawei.uxwidget.NotProguard;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * DetailTopView
 *
 * @author wxliao on 17/7/22.
 */
public class DetailTopView extends RelativeLayout implements View.OnClickListener {
    /**
     * COMMENT_DIV
     */
    public static final int COMMENT_DIV = 10000;
    /**
     * 书籍封面,书籍赞数量,书籍作者其它书籍,隐藏简介或者收起图片
     */
    private ImageView imageViewCover, ivDown;

    private TextView tvDetailVipTips, tvBookName, tvScore, tvCommentCount, tvAuthorName, tvRenewStatus, tvWordCount, tvOriginalPrice, tvCurrentPrice;
    private RelativeLayout reCp;
    private RelativeLayout rlBookImage;
    private RelativeLayout rlBookPrice;

    private CommentRatingBarView ratingBar;

    private ImageView viewBlur;
    private int blurBaseHeight = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public DetailTopView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DetailTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(Context context) {
        int width = (int) getResources().getDimension(R.dimen.hw_dp_192);
        int height = (int) getResources().getDimension(R.dimen.hw_dp_16);
        blurBaseHeight = (int) getResources().getDimension(R.dimen.hw_dp_20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        setLayoutParams(params);

        LayoutInflater.from(context).inflate(R.layout.view_book_detail_top, this, true);
        imageViewCover = findViewById(R.id.imageView_cover);
        tvBookName = findViewById(R.id.tv_bookName);
        rlBookImage = findViewById(R.id.rlBookImage);
        tvScore = findViewById(R.id.tvScore);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        ratingBar = findViewById(R.id.ratingbar);
        tvDetailVipTips = findViewById(R.id.tv_detail_vip_tips);
        tvAuthorName = findViewById(R.id.tvAuthorName);
        viewBlur = findViewById(R.id.viewBlur);
        ivDown = findViewById(R.id.iv_down);
        reCp = findViewById(R.id.re_cp);
        reCp.setOnClickListener(this);
        rlBookPrice = findViewById(R.id.rl_book_price);
        tvOriginalPrice = findViewById(R.id.tvBookPrice_original);
        tvCurrentPrice = findViewById(R.id.tvBookPrice_current);
        tvRenewStatus = findViewById(R.id.tvRenewSt);
        tvWordCount = findViewById(R.id.tvRenewSt_wordCount);
        tvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        TypefaceUtils.setHwChineseMediumFonts(tvBookName);
    }

    /**
     * 绑定数据
     *
     * @param result result
     */
    @SuppressLint("SetTextI18n")
    public void bindData(final BeanBookInfo result) {
        displayVipTips(result);
        displayImgBookCover(result);
        displayScore(result);
        displayBookName(result);
        displayCpAndAuthor(result);
        displayCommentCount(result);
        displayBookRenewSt(result);
    }

    private void displayVipTips(BeanBookInfo result) {
        if (!TextUtils.isEmpty(result.vipTips)) {
            tvDetailVipTips.setVisibility(View.VISIBLE);
            if (result.vipClickable == 1) {
                tvDetailVipTips.setOnClickListener(this);
            }
            tvDetailVipTips.setText(result.vipTips);
        }
    }

    private void displayImgBookCover(BeanBookInfo result) {
        String coverWap = result.coverWap;
        if (!TextUtils.isEmpty(coverWap)) {
            ImageConfig config = ImageConfig.create(imageViewCover).
                    with(getContext()).
                    setResource(coverWap).
                    setSkipMemoryCache(true).
                    setAnimMode(AnimMode.NULL);

            config.requestListener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    long num = 20;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = viewBlur.getDrawable();
                            if (drawable != null) {
                                return;
                            }
                            refreshShadow();
                        }
                    }, num);
                    return false;
                }
            };

            DataManager.getImageHelper().loadImage(config);
        }
    }

    private void displayScore(BeanBookInfo result) {
        if (!TextUtils.isEmpty(result.score)) {
            int score = 0;
            try {
                score = (int) (Float.parseFloat(result.score) + 0.5);
            } catch (Exception ignore) {

            }
            if (score > 0) {
                tvScore.setText(result.score + getResources().getString(R.string.how_many_points));
                float meg = MathUtils.meg(((float) score) / 2);
                ratingBar.setStar(meg);
            } else {
                tvScore.setVisibility(GONE);
                ratingBar.setVisibility(GONE);
            }
        } else {
            tvScore.setVisibility(GONE);
            ratingBar.setVisibility(GONE);
        }
    }

    private void displayBookName(BeanBookInfo result) {
        if (!TextUtils.isEmpty(result.bookName)) {
            tvBookName.setText(result.bookName);
        }
    }

    private void displayCpAndAuthor(BeanBookInfo result) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(result.cp) && !TextUtils.isEmpty(result.author)) {
            stringBuilder.append(result.cp);
            int length1 = stringBuilder.length();
            stringBuilder.append(" | ");
            int length2 = stringBuilder.length();
            stringBuilder.append(result.author.trim()).append(" ").append(getContext().getString(R.string.str_verb_writing));
            int length3 = stringBuilder.length();
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(CompatUtils.getColor(getContext(), R.color.color_50_000000));
            ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(CompatUtils.getColor(getContext(), R.color.color_100_d8d8d8));
            ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(CompatUtils.getColor(getContext(), R.color.color_ff000000));
            SpannableString spannableString = new SpannableString(stringBuilder.toString());
            spannableString.setSpan(colorSpan1, 0, length1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(colorSpan2, length1, length2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(colorSpan3, length2, length3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


            ViewTreeObserver vto = tvAuthorName.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = tvAuthorName.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    Layout layout = tvAuthorName.getLayout();
                    if (null != layout) {
                        int lines = layout.getLineCount();
                        if (lines - 1 > 0) {
                            //有省略
                            ivDown.setVisibility(VISIBLE);
                            reCp.setClickable(true);
                            // 收缩
                            tvAuthorName.setMaxLines(1);
                            tvAuthorName.setEllipsize(TextUtils.TruncateAt.END);

                        } else {
                            //无省略
                            reCp.setClickable(false);
                            ivDown.setVisibility(GONE);
                        }
                    }
                }
            });
            tvAuthorName.setText(spannableString);
        } else {
            reCp.setVisibility(GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayCommentCount(BeanBookInfo result) {
        if (!TextUtils.isEmpty(result.commentNum)) {
            int comment = Integer.parseInt(result.commentNum);
            if (comment > 0) {
                if (comment / COMMENT_DIV >= 1 && comment % COMMENT_DIV > 0) {
                    tvCommentCount.setText(comment / COMMENT_DIV + getContext().getString(R.string.str_score_tips1));
                } else if (comment / COMMENT_DIV >= 1) {
                    tvCommentCount.setText(comment / COMMENT_DIV + getContext().getString(R.string.str_score_tips2));
                } else {
                    tvCommentCount.setText(comment + getContext().getString(R.string.str_socre_tips3));
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayBookRenewSt(BeanBookInfo result) {
        if (!TextUtils.isEmpty(result.totalWordSize)) {
            if (result.status == 0) {
                tvRenewStatus.setText(getResources().getString(R.string.renew_status_0));
            } else {
                tvRenewStatus.setText(getResources().getString(R.string.renew_status_1));
            }
            tvWordCount.setText(result.totalWordSize + "字");
        }
        if (TextUtils.isEmpty(result.oldPrice) && TextUtils.isEmpty(result.currentPrice)) {
            rlBookPrice.setVisibility(GONE);
        } else {
            if (!TextUtils.isEmpty(result.oldPrice)) {
                tvOriginalPrice.setVisibility(VISIBLE);
                tvOriginalPrice.setText(result.oldPrice + " " + getResources().getString(R.string.person_top_remain));
            } else {
                tvOriginalPrice.setVisibility(GONE);
            }
            if (!TextUtils.isEmpty(result.currentPrice)) {
                tvCurrentPrice.setText(result.currentPrice + " " + getResources().getString(R.string.person_top_remain));
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v != null) {
            int id = v.getId();
            if (id == R.id.tv_detail_vip_tips) {
                MyVipActivity.launch(getContext());
            } else if (id == R.id.re_cp) {
                ivDown.setVisibility(GONE);
                reCp.setClickable(false);
                // 收缩
                tvAuthorName.setMaxLines(2);
                tvAuthorName.setEllipsize(TextUtils.TruncateAt.END);

            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtils.unregister(this);
    }


    /**
     * 刷新底部模糊层 后面要做到与Glide同步
     */
    @NotProguard
    public void refreshShadow() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {

            // 获取书籍封面外面的相对布局的DrawableCache
            rlBookImage.setDrawingCacheEnabled(true);
            Bitmap bitmap = rlBookImage.getDrawingCache();
            if (bitmap != null) {
                final Bitmap bitmap1 = Bitmap.createBitmap(bitmap);
                DzSchedulers.child(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void run() {
                        final Bitmap bitmap2 = blur(bitmap1);
                        DetailTopView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                viewBlur.setBackground(new BitmapDrawable(getResources(), bitmap2));
                            }
                        });
                    }
                });
            }
            rlBookImage.setDrawingCacheEnabled(false);
        }
    }

    /**
     * 按长方形裁切图片
     *
     * @param bitmap bitmap
     * @return bitmap
     */
    public Bitmap imageCropFromBottom(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        // 得到图片的宽，高
        int h = bitmap.getHeight();
        int nh = h;
        int retY = 0;
        if (h > blurBaseHeight) {
            nh = blurBaseHeight;
            retY = h - blurBaseHeight;
        }

        // 下面这句是关键
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, retY, bitmap.getWidth(), nh, null, false);
        if (!bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bmp;
    }

    /**
     * 高斯模糊
     *
     * @param bitmap bitmap
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blur(Bitmap bitmap) {
        long t = System.currentTimeMillis();
        bitmap = imageCropFromBottom(bitmap);

        int width = Math.round((float) bitmap.getWidth() * 0.5F);
        int height = Math.round((float) bitmap.getHeight() * 0.6F);
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, width, height, false);

        if (!bitmap.equals(bitmap1) && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap1);
        RenderScript mRender = RenderScript.create(getContext());
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(mRender, Element.U8_4(mRender));
        Allocation mAllocation1 = Allocation.createFromBitmap(mRender, bitmap1);
        Allocation mAllocation2 = Allocation.createFromBitmap(mRender, bitmap2);
        scriptIntrinsicBlur.setRadius(24.8F);
        scriptIntrinsicBlur.setInput(mAllocation1);
        scriptIntrinsicBlur.forEach(mAllocation2);
        mAllocation2.copyTo(bitmap2);

        scriptIntrinsicBlur.destroy();
        mAllocation1.destroy();
        mAllocation2.destroy();

        ALog.dLk("blur delay = " + (System.currentTimeMillis() - t));
        return bitmap2;
    }

}
