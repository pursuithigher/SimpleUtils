package com.dzbook.view.store;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ishugui.R;

import hw.sdk.net.bean.store.BeanTempletInfo;

/**
 * Tm1View
 */
public class Tm1View extends LinearLayout {

    private Context mContext;

    private TextView textView;

    /**
     * 构造
     *
     * @param context context
     */
    public Tm1View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Tm1View(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_tm1, this);
        textView = view.findViewById(R.id.textview);
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindData(BeanTempletInfo templetInfo) {
        if (templetInfo == null) {
            return;
        }
        textView.setText(templetInfo.title);
    }
}
