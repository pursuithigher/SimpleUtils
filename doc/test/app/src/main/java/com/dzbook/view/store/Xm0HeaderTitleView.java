package com.dzbook.view.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ishugui.R;

import hw.sdk.net.bean.store.BeanSubTempletInfo;

/**
 * Xm0HeaderTitleView
 *
 * @author dongdianzhou on 2018/3/19.
 */

public class Xm0HeaderTitleView extends RelativeLayout {

    private Context mContext;

    private TextView textViewXm1;
    private ImageView imageViewSelected;

    /**
     * 构造
     *
     * @param context context
     */
    public Xm0HeaderTitleView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Xm0HeaderTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {

    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(mContext).inflate(R.layout.view_xm0_headertitleview, this);
        textViewXm1 = findViewById(R.id.textview_xm1);
        imageViewSelected = findViewById(R.id.imageview_select);
    }

    /**
     * 绑定数据
     *
     * @param subTempletInfo subTempletInfo
     */
    public void bindData(BeanSubTempletInfo subTempletInfo) {
        if (subTempletInfo != null) {
            textViewXm1.setText(subTempletInfo.title);
            imageViewSelected.setVisibility(subTempletInfo.isXm0Selected ? VISIBLE : GONE);
            textViewXm1.setTextAppearance(mContext, subTempletInfo.isXm0Selected ? R.style.StoreFreeTextViewSelected : R.style.StoreFreeTextViewUnSelect);
        }
    }
}
