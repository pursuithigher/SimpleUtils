package com.views.ui.scroller;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

public class ScrollerTextView extends AppCompatTextView {
    private Scroller mScroller;
    float rawX;
    float rawY;

    public ScrollerTextView(Context context) {
        super(context);
        init(context);
    }

    public ScrollerTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                rawY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                ViewCompat.offsetTopAndBottom(this, (int) (event.getRawY() - rawY));
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
    }
}
