package com.example.myapplication;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class ExpandTextView extends AppCompatTextView {

    private int defaultMaxLine = 4;

    private boolean isExpand = false;

    public ExpandTextView(Context context) {
        super(context);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null) {
            if (!isExpand && layout.getLineCount() >= defaultMaxLine) {
                int top = layout.getLineTop(defaultMaxLine);
                setMeasuredDimension(getMeasuredWidth(), top);
            }
        }
    }
}
