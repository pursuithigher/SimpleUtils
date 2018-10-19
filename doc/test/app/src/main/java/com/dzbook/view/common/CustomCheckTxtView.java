package com.dzbook.view.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.ImageUtils;
import com.ishugui.R;

/***
 * 自定义TextView样式 (自备底部横线)
 * @author wangjianchen
 */
public class CustomCheckTxtView extends LinearLayout {

    private TextView textView;

    private boolean isSelected;
    private boolean isSupportLine;

    private Paint mLinePaint;

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            isSelected = !isSelected;
        }
    };

    /**
     * 构造
     *
     * @param context context
     */
    public CustomCheckTxtView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CustomCheckTxtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setOnClickListener(onClickListener);

        initCheckHintView();

        mLinePaint = new Paint(getChildCount());
        mLinePaint.setColor(Color.parseColor("#33182233"));
    }

    private void initCheckHintView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView = new TextView(getContext());
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setMaxLines(1);
        // 护眼模式适配
        if (SettingManager.getInstance(getContext()).getReaderEyeMode()) {
            int readerEyeColor = ImageUtils.getBlueFilterColor();
            textView.setTextColor(ImageUtils.mixColor(CompatUtils.getColor(getContext(), R.color.color_100_1A1A1A), readerEyeColor));
        } else {
            textView.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_1A1A1A));
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        addView(textView, layoutParams);
    }

    public void setChecked(boolean checked) {
        this.isSelected = checked;
    }


    /**
     * item底部划线
     *
     * @param supportLine supportLine
     */
    public void setSupportLine(boolean supportLine) {
        if (this.isSupportLine != supportLine) {
            this.isSupportLine = supportLine;
            if (supportLine) {
                invalidate();
            }
        }
    }

    /**
     * 描述文字
     *
     * @param desc desc
     */
    public void setDesc(String desc) {
        if (textView != null) {
            textView.setText(desc);
        }
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isSupportLine) {
            canvas.drawRect(getLeft(), getMeasuredHeight() - 1, getMeasuredWidth() - getPaddingRight(), getMeasuredHeight(), mLinePaint);
        }
    }

}
