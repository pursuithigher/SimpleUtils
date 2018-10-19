package com.dzbook.view.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * ModuleItemView
 *
 * @author dongdianzhou on 2017/12/15.
 */

public class ModuleItemView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTextView;
    private TextView mTextViewMark;

    private int templetPosition;
    private long clickDelayTime = 0;
    private BeanTempletInfo templetInfo;
    private BeanSubTempletInfo subTempletInfo;
    private TempletPresenter mTempletPresenter;


    /**
     * 构造
     *
     * @param context         context
     * @param templetPosition templetPosition
     */
    public ModuleItemView(Context context, int templetPosition) {
        this(context, null);
        this.templetPosition = templetPosition;
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ModuleItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
                    if (subTempletInfo != null && mTempletPresenter != null) {
                        mTempletPresenter.setActionClick(subTempletInfo, templetInfo, templetPosition, TempletPresenter.LOG_CLICK_ACTION_FL0);
                    }
                }
                clickDelayTime = current;
            }
        });
    }

    private void initData() {

    }

    private void initView() {
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_module_item, this);
        mImageView = view.findViewById(R.id.imageview_icon);
        mTextView = view.findViewById(R.id.textview_title);
        mTextViewMark = view.findViewById(R.id.textview_mark);
    }

    /**
     * 绑定数据
     *
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     * @param sub              sub
     * @param templetInfo1     templetInfo1
     */
    public void bindData(Fragment fragment, TempletPresenter templetPresenter, BeanSubTempletInfo sub, BeanTempletInfo templetInfo1) {
        mTempletPresenter = templetPresenter;
        subTempletInfo = sub;
        this.templetInfo = templetInfo1;
        mTextView.setText(sub.title);
        if (!TextUtils.isEmpty(sub.subScript)) {
            mTextViewMark.setText(sub.subScript);
            mTextViewMark.setVisibility(VISIBLE);

        } else {
            mTextViewMark.setVisibility(GONE);
        }
        String iconUrl = "";
        if (sub.imgUrl != null && sub.imgUrl.size() > 0) {
            iconUrl = sub.imgUrl.get(0);
        }
        if (!TextUtils.isEmpty(iconUrl)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, iconUrl, -10);
        }
    }
}
