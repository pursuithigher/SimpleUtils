package com.dzbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * ComScrollView
 *
 * @author wz on 2016/5/26 0026.
 */
public class ComScrollView extends ScrollView {
    private ComScrollViewListener mComScrollViewListener = null;

    /**
     * 构造
     *
     * @param context context
     */
    public ComScrollView(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public ComScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ComScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollViewListener(ComScrollViewListener scrollViewListener) {
        this.mComScrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mComScrollViewListener != null) {
            mComScrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }


}
