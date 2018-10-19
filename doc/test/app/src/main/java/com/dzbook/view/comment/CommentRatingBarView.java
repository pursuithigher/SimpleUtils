package com.dzbook.view.comment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ishugui.R;

import java.math.BigDecimal;

/**
 * CommentRatingBarView
 *
 * @author Winzows on 2017/11/27.
 */
public class CommentRatingBarView extends LinearLayout {
    private boolean mClickable;
    private boolean halfstart;
    private int starCount;
    private int starNum;
    private OnRatingChangeListener onRatingChangeListener;
    private float starImageWidth;
    private float starImageHeight;
    private float starImagePaddingLeft, starImagePaddingTop, starImagePaddingRight, starImagePaddingBottom;
    private Drawable starEmptyDrawable;
    private Drawable starFillDrawable;
    private Drawable starHalfDrawable;
    private int y = 1;
    private boolean isEmpty = true;

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CommentRatingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CommentRatingBarView);

        starHalfDrawable = mTypedArray.getDrawable(R.styleable.CommentRatingBarView_starHalf);
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.CommentRatingBarView_starEmpty);
        starFillDrawable = mTypedArray.getDrawable(R.styleable.CommentRatingBarView_starFill);
        starImageWidth = mTypedArray.getDimension(R.styleable.CommentRatingBarView_starImageWidth, 60);
        starImageHeight = mTypedArray.getDimension(R.styleable.CommentRatingBarView_starImageHeight, 120);
        starImagePaddingLeft = mTypedArray.getDimension(R.styleable.CommentRatingBarView_starImagePaddingLeft, 0);
        starImagePaddingTop = mTypedArray.getDimension(R.styleable.CommentRatingBarView_starImagePaddingTop, 0);
        starImagePaddingRight = mTypedArray.getDimension(R.styleable.CommentRatingBarView_starImagePaddingRight, 0);
        starImagePaddingBottom = mTypedArray.getDimension(R.styleable.CommentRatingBarView_starImagePaddingBottom, 0);
        starCount = mTypedArray.getInteger(R.styleable.CommentRatingBarView_starCount, 5);
        starNum = mTypedArray.getInteger(R.styleable.CommentRatingBarView_starNum, 0);
        mClickable = mTypedArray.getBoolean(R.styleable.CommentRatingBarView_clickable, true);
        halfstart = mTypedArray.getBoolean(R.styleable.CommentRatingBarView_halfstart, false);

        for (int i = 0; i < starNum; ++i) {
            ImageView imageView = getStarImageView(context, false);
            addView(imageView);
        }
        final float num = 0.5f;
        for (int i = 0; i < starCount; ++i) {
            ImageView imageView = getStarImageView(context, isEmpty);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickable) {
                        if (halfstart) {
                            //TODO:This is not the best way to solve half a star,
                            //TODO:but That's what I can do,Please let me know if you have a better solution
                            if (y % 2 == 0) {
                                setStar(indexOfChild(v) + 1f);
                            } else {
                                setStar(indexOfChild(v) + num);
                            }
                            if (onRatingChangeListener != null) {
                                if (y % 2 == 0) {
                                    onRatingChangeListener.onRatingChange(indexOfChild(v) + 1f);
                                    y++;
                                } else {
                                    onRatingChangeListener.onRatingChange(indexOfChild(v) + num);
                                    y++;
                                }
                            }
                        } else {
                            setStar(indexOfChild(v) + 1f);
                            if (onRatingChangeListener != null) {
                                onRatingChangeListener.onRatingChange(indexOfChild(v) + 1f);
                            }
                        }

                    }

                }
            });
            addView(imageView);
        }

    }

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    /**
     * 设置尺寸
     *
     * @param starImageSize starImageSize
     */
    public void setStarImageSize(float starImageSize) {
    }

    public void setStarImageWidth(float starImageWidth) {
        this.starImageWidth = starImageWidth;
    }

    public void setStarImageHeight(float starImageHeight) {
        this.starImageHeight = starImageHeight;
    }


    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }


    private ImageView getStarImageView(Context context, boolean isEmpty1) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams para = new ViewGroup.LayoutParams(Math.round(starImageWidth), Math.round(starImageHeight));
        imageView.setLayoutParams(para);
        imageView.setPadding(Math.round(starImagePaddingLeft), Math.round(starImagePaddingTop), Math.round(starImagePaddingRight), Math.round(starImagePaddingBottom));
        if (isEmpty1) {
            imageView.setImageDrawable(starEmptyDrawable);
        } else {
            imageView.setImageDrawable(starFillDrawable);
        }
        return imageView;
    }

    /**
     * 设置星标
     *
     * @param star starCount
     */
    public void setStar(float star) {
        if (star > 5) {
            star = 5;
        }
        if (star < 0) {
            star = 0;
        }

        int fint = (int) star;
        BigDecimal b1 = new BigDecimal(Float.toString(star));
        BigDecimal b2 = new BigDecimal(Integer.toString(fint));
        float fPoint = b1.subtract(b2).floatValue();


        star = fint > this.starCount ? this.starCount : fint;
        star = star < 0 ? 0 : star;

        //drawfullstar
        for (int i = 0; i < star; ++i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starFillDrawable);
        }

        //drawhalfstar
        if (fPoint > 0) {
            ((ImageView) getChildAt(fint)).setImageDrawable(starHalfDrawable);

            //drawemptystar
            for (int i = this.starCount - 1; i >= star + 1; --i) {
                ((ImageView) getChildAt(i)).setImageDrawable(starEmptyDrawable);
            }

        } else {
            //drawemptystar
            for (int i = this.starCount - 1; i >= star; --i) {
                ((ImageView) getChildAt(i)).setImageDrawable(starEmptyDrawable);
            }

        }

    }

    /**
     * change start listener
     */
    public interface OnRatingChangeListener {

        /**
         * onRatingChange
         *
         * @param ratingCount ratingCount
         */
        void onRatingChange(float ratingCount);

    }

}