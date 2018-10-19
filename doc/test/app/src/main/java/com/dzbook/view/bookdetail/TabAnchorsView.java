package com.dzbook.view.bookdetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.ElasticScrollView;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * TabAnchorsView
 *
 * @author caimantang on 2018/4/25.
 */

public class TabAnchorsView extends LinearLayout implements View.OnClickListener {
    /**
     * 简介
     */
    public static final int VIEW_BRIEF = 1;
    /**
     * 评论
     */
    public static final int VIEW_COMMENT = 2;
    /**
     * 推荐
     */
    public static final int VIEW_RECOMMEND = 3;
    /**
     * 评论数
     */
    private static final int MAX_COMMENT_NUM = 999;

    private TextView tvBrief, tvComment, tvRecommend;
    private ElasticScrollView scrollView;
    /**
     * 是否是悬浮的
     */
    private boolean mIsSuspension;
    private int measuredWidth;
    private boolean isOversizeComment = false;
    private View topView;
    private View briefView;
    private View commentView;
    private View recommendView;

    private int mType = -1;


    /**
     * 构造
     *
     * @param context context
     */
    public TabAnchorsView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public TabAnchorsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TabAnchorsView, 0, 0);
            if (null != typedArray) {
                mIsSuspension = typedArray.getBoolean(R.styleable.TabAnchorsView_isSuspension, false);
                typedArray.recycle();
            }
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);


        LayoutInflater.from(context).inflate(R.layout.view_tab_anchors, this, true);
        tvBrief = findViewById(R.id.tv_brief);
        tvComment = findViewById(R.id.tv_comment);
        tvRecommend = findViewById(R.id.tv_recommend);
        setListener();
        TypefaceUtils.setHwChineseMediumFonts(tvBrief);

        setPadding(0, 0, 0, 0);
        if (mIsSuspension) {
            setBackgroundResource(R.color.color_100_f2f2f2);
            tvComment.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (measuredWidth == 0) {
                        measuredWidth = tvComment.getMeasuredWidth();
                        int dip2px = DimensionPixelUtil.dip2px(getContext(), 48);
                        if (dip2px > 0 && measuredWidth > dip2px) {
                            isOversizeComment = true;
                            tvComment.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            //说明 需要包裹，为了保证改变字体时 数字跳动的bug，先对measuredHeight进行增加1dp
                            LayoutParams layoutParams = new LayoutParams(measuredWidth + DimensionPixelUtil.dip2px(getContext(), 2), DimensionPixelUtil.dip2px(getContext(), 32));
                            layoutParams.leftMargin = DimensionPixelUtil.dip2px(getContext(), 23);
                            layoutParams.rightMargin = DimensionPixelUtil.dip2px(getContext(), 23);
                            tvComment.setLayoutParams(layoutParams);
                        }
                    }
                }
            });
        }
    }

    private void setListener() {
        tvBrief.setOnClickListener(this);
        tvComment.setOnClickListener(this);
        tvRecommend.setOnClickListener(this);
        setOnClickListener(this);
    }


    /**
     * 设置选中项
     *
     * @param type type
     */
    public void setCurrentItem(int type) {
        mType = type;
        switch (type) {
            case VIEW_BRIEF:
                setSelect(tvBrief, type);
                unSetSelect(tvComment);
                unSetSelect(tvRecommend);
                break;
            case VIEW_COMMENT:
                unSetSelect(tvBrief);
                setSelect(tvComment, type);
                unSetSelect(tvRecommend);
                break;
            case VIEW_RECOMMEND:
                unSetSelect(tvBrief);
                unSetSelect(tvComment);
                setSelect(tvRecommend, type);
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_brief:
                smoothScrollToY(briefView.getBottom() - getMeasuredHeight());
                break;
            case R.id.tv_comment:
                smoothScrollToY(commentView.getBottom() - getMeasuredHeight());
                break;
            case R.id.tv_recommend:
                smoothScrollToY(recommendView.getBottom() - getMeasuredHeight());
                break;
            default:
                break;
        }
    }


    /**
     * 设置评论数
     *
     * @param commentNum commentNum
     */
    @SuppressLint("SetTextI18n")
    public void setCommentNumb(String commentNum) {
        if (!TextUtils.isEmpty(commentNum)) {
            int num = 0;
            try {
                num = Integer.parseInt(commentNum.trim());
                if (num > MAX_COMMENT_NUM) {
                    commentNum = "999+";
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            tvComment.setText(getResources().getString(R.string.book_comment_only_simplified_chinese) + "(" + commentNum + ")");
        }
    }

    /**
     * setScrollView
     *
     * @param scrollView1    scrollView1
     * @param topView1       topView1
     * @param briefView1     briefView1
     * @param commentView1   commentView1
     * @param recommendView1 recommendView1
     */
    public void setScrollView(ElasticScrollView scrollView1, View topView1, View briefView1, View commentView1, View recommendView1) {
        this.scrollView = scrollView1;
        this.topView = topView1;
        this.briefView = briefView1;
        this.commentView = commentView1;
        this.recommendView = recommendView1;
    }

    private void smoothScrollToY(final int offsetY) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (null != scrollView) {
                    scrollView.scrollTo(0, offsetY);
                }
            }
        });
    }

    /**
     * 设置选中
     *
     * @param textView textView
     * @param type     type
     */
    public void setSelect(TextView textView, int type) {
        TypefaceUtils.setHwChineseMediumFonts(textView);
        int color = CompatUtils.getColor(getContext(), R.color.color_100_CD2325);
        textView.setTextColor(color);
        textView.setBackgroundResource(R.drawable.hw_tab_layer);
    }

    /**
     * 非选中
     *
     * @param textView textView
     */
    public void unSetSelect(TextView textView) {
        TypefaceUtils.setRegularFonts(textView);
        int color = CompatUtils.getColor(getContext(), R.color.color_50_1A1A1A);
        textView.setTextColor(color);
        textView.setBackground(null);
    }

    /**
     * 滚动监听
     *
     * @param x      x
     * @param y      y
     * @param offset offset
     */
    public void onScrollChanged(int x, int y, int offset) {
        if (y > topView.getHeight()) {
            if (mIsSuspension) {
                setVisibility(VISIBLE);
            } else {
                setVisibility(INVISIBLE);
            }
        } else {
            if (mIsSuspension) {
                setVisibility(GONE);
            } else {
                setVisibility(VISIBLE);
            }
        }
        if (mIsSuspension) {
            if (y >= recommendView.getBottom() - offset) {
                if (mType != VIEW_RECOMMEND) {
                    setCurrentItem(VIEW_RECOMMEND);
                }
            } else if (y >= commentView.getBottom() - offset) {
                if (mType != VIEW_COMMENT) {
                    setCurrentItem(VIEW_COMMENT);
                }
            } else if (y >= briefView.getBottom() - offset) {
                if (mType != VIEW_BRIEF) {
                    setCurrentItem(VIEW_BRIEF);
                }
            }
        }
    }

    /**
     * 设置数据
     *
     * @param bookInfo bookInfo
     */
    public void bindData(BeanBookInfo bookInfo) {
        if (null != bookInfo && !TextUtils.isEmpty(bookInfo.commentNum)) {
            try {
                int anInt = Integer.parseInt(bookInfo.commentNum);
                if (anInt == 0) {
                    return;
                }
                int numb = anInt / 10000;
                if (numb > 0) {
                    setCommentNumb(numb + "w+");
                } else {
                    setCommentNumb(anInt + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
