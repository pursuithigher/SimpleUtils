package com.dzbook.view.shelf;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import hw.sdk.utils.UiHelper;

/**
 * ShelfGridBkView
 */
public class ShelfGridBkView extends RelativeLayout {

    /**
     * 构造
     *
     * @param context context
     */
    public ShelfGridBkView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ShelfGridBkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int gridItemNum;

        if (DeviceUtils.isPad(context)) {
            gridItemNum = 4;
        } else {
            gridItemNum = 3;
        }
        int width = UiHelper.getScreenWidth(context) - DimensionPixelUtil.dip2px(context, 48 + 21 * (gridItemNum - 1));
        int height = width / gridItemNum * 120 / 90 + DimensionPixelUtil.dip2px(context, 35);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        LayoutInflater.from(context).inflate(R.layout.view_shelfgridbk, this);
    }
}
