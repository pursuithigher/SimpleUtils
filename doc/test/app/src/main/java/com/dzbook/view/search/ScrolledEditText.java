package com.dzbook.view.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * ScrolledEditText
 *
 * @author dongdianzhou on 2017/9/27.
 */

public class ScrolledEditText extends EditText {

    /**
     * 构造
     *
     * @param context context
     */
    public ScrolledEditText(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ScrolledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        int line = getLineCount();
        if (line > 4) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return result;
    }

}
