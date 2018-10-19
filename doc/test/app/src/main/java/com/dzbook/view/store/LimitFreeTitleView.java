package com.dzbook.view.store;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * 限免标题栏
 *
 * @author dongdianzhou on 2018/1/9.
 */

public class LimitFreeTitleView extends RelativeLayout implements View.OnClickListener {

    private Context mContext;

    private TempletPresenter mPresenter;

    private TextView mTextViewTitle;
    private CountDownTextView mTextViewTime;
    private TextView mTextViewMore;
    private ImageView mImageViewLeft;

    private int templetPosition;
    private long clickDelayTime = 0;
    private BeanTempletInfo mTempletInfo;

    /**
     * 构造
     *
     * @param context          context
     * @param templetPresenter templetPresenter
     * @param templetPosition  templetPosition
     * @param hasFlTab         hasFlTab
     */
    public LimitFreeTitleView(Context context, TempletPresenter templetPresenter, int templetPosition, boolean hasFlTab) {
        this(context, null, hasFlTab);
        mPresenter = templetPresenter;
        this.templetPosition = templetPosition;
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param hasFlTab hasFlTab
     */
    public LimitFreeTitleView(Context context, AttributeSet attrs, boolean hasFlTab) {
        super(context, attrs);
        mContext = context;
        initView(hasFlTab);
        initData();
        setListener();
    }

    private void setListener() {
        mImageViewLeft.setOnClickListener(this);
        mTextViewMore.setOnClickListener(this);
        mTextViewTime.setCountDownListener(new CountDownTextView.CountDownListener() {
            @Override
            public void countdown() {
                mTextViewTime.setVisibility(GONE);
            }
        });
    }

    private void initData() {

    }

    private void initView(boolean hasFlTab) {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (hasFlTab) {
            layoutParams.topMargin = DimensionPixelUtil.dip2px(mContext, 25);
        } else {
            layoutParams.topMargin = DimensionPixelUtil.dip2px(mContext, 8);
        }
        //        layoutParams.bottomMargin = DimensionPixelUtil.dip2px(mContext, 8);
        layoutParams.leftMargin = DimensionPixelUtil.dip2px(mContext, 16);
        layoutParams.rightMargin = DimensionPixelUtil.dip2px(mContext, 18);
        setLayoutParams(layoutParams);
        LayoutInflater.from(getContext()).inflate(R.layout.view_limitfree_title, this);
        mTextViewTitle = findViewById(R.id.textview_title);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewTitle);
        mTextViewTime = findViewById(R.id.textview_time);
        mTextViewMore = findViewById(R.id.textview_more);
        mImageViewLeft = findViewById(R.id.imageview_jiantou);
    }

    @Override
    public void onClick(View v) {
        long current = System.currentTimeMillis();
        if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
            int id = v.getId();
            if (id == R.id.textview_more || id == R.id.imageview_jiantou) {
                String firstTabId = "";
                if (0 < mTempletInfo.items.size()) {
                    BeanSubTempletInfo subTempletInfo = mTempletInfo.items.get(0);
                    if (subTempletInfo != null) {
                        firstTabId = subTempletInfo.id;
                    }
                }
                mPresenter.skipToLimitFree(mTempletInfo.title, mTempletInfo.action.dataId, firstTabId);
                mPresenter.logXMBookClick(TempletPresenter.LOG_CLICK_ACTION_XM0, TempletPresenter.LOG_CLICK_MORE, mTempletInfo, templetPosition);
                mPresenter.logHw(mTempletInfo, null, templetPosition, TempletPresenter.LOG_CLICK_ACTION_XM0, TempletPresenter.LOG_CLICK_MORE, true);
            }
        }
        clickDelayTime = current;
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindData(BeanTempletInfo templetInfo) {
        if (templetInfo != null) {
            mTempletInfo = templetInfo;
            mTextViewTitle.setText(templetInfo.title);
            mTextViewTime.bindData(templetInfo.counter);
        }
    }
}
