package com.dzbook.view.store;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * SigleBooKViewV
 *
 * @author dongdianzhou on 2018/1/9.
 */

public class SigleBooKViewV extends ConstraintLayout {

    private int templetPosition;
    private TempletPresenter mTempletPresenter;
    private long clickDelayTime = 0;
    private ImageView mImageView;
    private TextView mTextView;
    private TextView mTextViewAuthor;
    private BeanSubTempletInfo sub;
    private BeanTempletInfo templetInfo;
    private boolean isLimitFree;
    private int logClickAction;

    /**
     * 构造
     *
     * @param context context
     */
    public SigleBooKViewV(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SigleBooKViewV(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    public void setTempletPresenter(TempletPresenter templetPresenter) {
        this.mTempletPresenter = templetPresenter;
    }

    public TempletPresenter getTempletPresenter() {
        return mTempletPresenter;
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
    }

    private void click() {
        long current = System.currentTimeMillis();
        if (current - clickDelayTime > TempletContant.CLICK_DISTANSE && sub != null) {
            clickDelayTime = current;
            if (isLimitFree) {
                mTempletPresenter.skipToLimitFree(templetInfo.title, templetInfo.action.dataId, templetInfo.tabId);
                mTempletPresenter.logXMBookClick(logClickAction, TempletPresenter.LOG_CLICK_BOOK, templetInfo, templetPosition);
                mTempletPresenter.logHw(templetInfo, sub, templetPosition, logClickAction, TempletPresenter.LOG_CLICK_BOOK, true);
            } else {
                mTempletPresenter.skipToBookDetail(sub.id, sub.title);
                mTempletPresenter.logCommonBookClick(logClickAction, TempletPresenter.LOG_CLICK_BOOK, templetInfo, sub.id, templetPosition);
                mTempletPresenter.logHw(templetInfo, sub, templetPosition, logClickAction, TempletPresenter.LOG_CLICK_BOOK, true);
            }
        }
    }

    private void initData() {

    }


    private void initView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        LayoutInflater.from(getContext()).inflate(R.layout.view_siglebookviewv, this);
        mImageView = findViewById(R.id.imageview_book);
        mTextView = findViewById(R.id.textview_title);
        mTextViewAuthor = findViewById(R.id.textview_author);
    }

    /**
     * 绑定数据
     *
     * @param subTempletInfo   subTempletInfo
     * @param templetInfo1     templetInfo
     * @param isLimitFree1     isLimitFree
     * @param logClickAction1  logClickAction
     * @param templetPosition1 templetPosition
     */
    public void bindData(BeanSubTempletInfo subTempletInfo, BeanTempletInfo templetInfo1, boolean isLimitFree1, int logClickAction1, int templetPosition1) {
        this.templetPosition = templetPosition1;
        this.logClickAction = logClickAction1;
        this.templetInfo = templetInfo1;
        this.isLimitFree = isLimitFree1;
        sub = subTempletInfo;
        mTextView.setText(subTempletInfo.title);
        mTextViewAuthor.setText(subTempletInfo.author);
        String imgUrl = "";
        if (subTempletInfo.imgUrl != null && subTempletInfo.imgUrl.size() > 0) {
            imgUrl = subTempletInfo.imgUrl.get(0);
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, imgUrl, 0);
        }
    }


    /**
     * 清除imageview
     */
    public void clearImageView() {
        if (mImageView != null) {
            Glide.with(getContext()).clear(mImageView);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, null, 0);
        }
    }
}
