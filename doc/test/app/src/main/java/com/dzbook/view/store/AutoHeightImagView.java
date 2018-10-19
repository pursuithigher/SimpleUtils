package com.dzbook.view.store;

import android.content.Context;
import android.util.AttributeSet;

import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.SelectableRoundedImageView;

/**
 * 自适应高度ImageView
 *
 * @author dongdz
 */
public class AutoHeightImagView extends SelectableRoundedImageView {
    /**
     * 构造
     *
     * @param context content
     */
    public AutoHeightImagView(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context content
     * @param attrs   attrs
     */
    public AutoHeightImagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Context context = getContext();
        if (context != null) {
            int padding = DimensionPixelUtil.dip2px(context, 16);
            float margin = DimensionPixelUtil.dip2px(context, DeviceUtils.isPad(context) ? 12 : 8);
            int screenWidth = DeviceInfoUtils.getInstanse().getWidthReturnInt();
            int viewWidth = (int) ((screenWidth - padding * 2 - margin) / 2);
            int height = viewWidth * 88 / 160;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
