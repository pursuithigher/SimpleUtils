package com.dzbook.view.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.utils.MathUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;

/**
 * showType
 * 封装评论view  公用一个view  处理相似的逻辑。
 * <p>
 *
 * @author Winzows on 2017/12/5.
 */

public class CommentItemView extends CommentBaseView implements View.OnClickListener {

    private ImageView circlePhoto, imageViewLike, ivCover;
    private TextView tvUserName, tvAuthor, tvContent, tvTime, textViewLike, tvBookName, tvVip, tvStatusTips;
    private CommentRatingBarView ratingBar;

    private View vLine;

    private LinearLayout llLike;

    private int pageType = -1;

    private AnimationSet animationSet;
    private View reBook;


    /**
     * 构造
     *
     * @param context context
     */
    public CommentItemView(@NonNull Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CommentItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 暴露出方法 绑定data
     *
     * @param isShowEndLine isShowEndLine
     * @param pageType1     pageType1
     * @param info          info
     */
    @SuppressLint("SetTextI18n")
    public void bindData(int pageType1, BeanCommentInfo info, boolean isShowEndLine) {
        if (this.pageType <= 0) {
            bindView(pageType1);
        }
        clearData();
        if (info != null) {
            this.info = info;
            setText(tvUserName, info.uName);
            setText(tvContent, info.content);
            setText(tvTime, info.date);
            setText(tvBookName, info.bookName);
            if (!TextUtils.isEmpty(info.author)) {
                setText(tvAuthor, info.author + " " + getResources().getString(R.string.str_verb_writing));
            }
            initLikeNum(textViewLike);
            setVisibility(tvVip, info.vip ? VISIBLE : GONE);
            setPraised(imageViewLike, info.praise);
            setStar(ratingBar, MathUtils.meg(getStarNum(info) / 2));
            setcover(circlePhoto, info.url, R.drawable.hw_avatar);
            setcover(ivCover, info.coverWap, 0);
            setViewVisibility(info);
            if (null != vLine) {
                if (!isShowEndLine) {
                    vLine.setVisibility(View.VISIBLE);
                } else {
                    vLine.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setViewVisibility(BeanCommentInfo info) {
        if (tvStatusTips != null) {
            if (null != info && info.commentStatus == 2) {
                tvStatusTips.setVisibility(VISIBLE);
                tvStatusTips.setText(R.string.comment_failed_pass);
            } else {
                tvStatusTips.setVisibility(GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void clearData() {
        setText(tvContent, "");
        setText(tvTime, "");
        setText(textViewLike, "");
        setText(tvUserName, "");
        setText(tvBookName, "");
        setVisibility(tvVip, GONE);
        if (null != ratingBar) {
            ratingBar.setStar(5);
        }
        if (imageViewLike != null) {
            imageViewLike.setBackgroundResource(R.drawable.hw_ic_comment_praise_cancel);
        }
    }

    private void initListener() {
        setOnClick(reBook);
        setOnClick(imageViewLike);
        setOnClick(llLike);
        setOnClick(textViewLike);
    }

    private void setOnClick(View view) {
        if (null != view) {
            view.setOnClickListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_like:
            case R.id.imageView_like:
            case R.id.ll_like:
                if (!NetworkUtils.getInstance().checkNet()) {
                    if (getContext() instanceof BaseActivity) {
                        ((BaseActivity) getContext()).showNotNetDialog();
                    }
                } else {
                    LoginUtils.getInstance().forceLoginCheck(getContext(), new LoginUtils.LoginCheckListener() {
                        @Override
                        public void loginComplete() {
                            clickLike(textViewLike, imageViewLike, animationSet);
                        }
                    });
                }

                break;
            case R.id.re_book:
                //去图书详情
                if (null != info) {
                    toBookDetail(info.bookId, info.bookName);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化动画
     */
    public void initAnimation() {
        final float num1 = 1.2f;
        final float num2 = 0.5f;
        ScaleAnimation scaleAmp = new ScaleAnimation(1.0f, num1, 1.0f, num1, ScaleAnimation.RELATIVE_TO_SELF, num2, ScaleAnimation.RELATIVE_TO_SELF, num2);
        ScaleAnimation scaleSmall = new ScaleAnimation(num1, 1.0f, num1, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, num2, ScaleAnimation.RELATIVE_TO_SELF, num2);

        animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAmp);
        animationSet.addAnimation(scaleSmall);
        scaleAmp.setDuration(200);
        scaleSmall.setDuration(200);
        scaleSmall.setStartOffset(200);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (imageViewLike != null && info != null) {
                    int ivRes = info.praise ? R.drawable.hw_ic_comment_praise : R.drawable.hw_ic_comment_praise_cancel;
                    int type = info.praise ? TYPE_PRAISE : TYPE_CANCLE_PRAISE;
                    operationComment(getContext(), info, type);
                    imageViewLike.setBackgroundResource(ivRes);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initBookDetailComment() {
        circlePhoto = findViewById(R.id.circle_photo);
        tvUserName = findViewById(R.id.tv_userName);
        ratingBar = findViewById(R.id.ratingbar);
        tvContent = findViewById(R.id.tv_content);
        tvTime = findViewById(R.id.tv_time);
        textViewLike = findViewById(R.id.textView_like);
        imageViewLike = findViewById(R.id.imageView_like);
        llLike = findViewById(R.id.ll_like);
        tvVip = findViewById(R.id.tvVip);

        TypefaceUtils.setHwChineseMediumFonts(tvUserName);
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    /**
     * 绑定view
     *
     * @param pageType1 pageType
     */
    public void bindView(int pageType1) {
        if (this.pageType > 0) {
            return;
        }
        setPageType(pageType1);
        setBackgroundResource(R.drawable.selector_hw_list_item);
        switch (this.pageType) {
            case TYPE_ITEM_BOOKDETAIL:
            case TYPE_ITEM_MORE_COMMENT:
                LayoutInflater.from(getContext()).inflate(R.layout.view_bookcomment_detail, this, true);
                vLine = findViewById(R.id.v_line);
                initBookDetailComment();
                break;
            case TYPE_ITEM_COMMENT_DETAIL:
                LayoutInflater.from(getContext()).inflate(R.layout.view_bookcomment_item_detail, this, true);
                initBookDetailComment();
                break;
            case TYPE_ITEM_PERSON_CENTER:
                LayoutInflater.from(getContext()).inflate(R.layout.view_bookcomment_my, this, true);
                ratingBar = findViewById(R.id.ratingbar);
                tvContent = findViewById(R.id.tv_content);
                tvTime = findViewById(R.id.tv_time);
                tvBookName = findViewById(R.id.tv_bookName);
                reBook = findViewById(R.id.re_book);
                tvAuthor = findViewById(R.id.tv_author);
                ivCover = findViewById(R.id.iv_cover);
                tvStatusTips = findViewById(R.id.tv_status_tips);
                vLine = findViewById(R.id.v_line);
                break;

            case TYPE_ITEM_MY_COMMENT_DETAIL:
                LayoutInflater.from(getContext()).inflate(R.layout.view_comment_detail, this, true);
                reBook = findViewById(R.id.re_book);
                ivCover = findViewById(R.id.iv_cover);
                tvBookName = findViewById(R.id.tv_bookName);
                tvAuthor = findViewById(R.id.tv_author);
                tvTime = findViewById(R.id.tv_time);
                tvContent = findViewById(R.id.tv_content);
                ratingBar = findViewById(R.id.ratingbar);
                TypefaceUtils.setHwChineseMediumFonts(tvBookName);
                break;
            default:
                break;
        }
        initListener();
        initAnimation();
    }
}
