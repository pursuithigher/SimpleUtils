package com.dzbook.activity.reader;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Base PopupWindow
 *
 * @author zhenglk
 */
public abstract class BasePopupWindow extends PopupWindow {

    /**
     * 构造
     *
     * @param context context
     */
    public BasePopupWindow(Context context) {
        super(context);
        setFocusable(true);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView(view);
        initData(view);
        setListener(view);
    }

    /**
     * 初始化view
     *
     * @param view view
     */
    protected abstract void initView(View view);

    /**
     * 初始化数据
     *
     * @param view view
     */
    protected abstract void initData(View view);

    /**
     * 设置回调
     *
     * @param view view
     */
    protected abstract void setListener(View view);
}
