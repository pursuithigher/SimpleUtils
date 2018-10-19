package com.dzbook.view.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.pay.LoadBookListener;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * Db1View
 *
 * @author dongdianzhou on 2018/1/6.
 */

public class Db1View extends RelativeLayout implements View.OnClickListener {

    private TextView mTextViewDel;
    private ProgressBar mSaleProgressView;
    private ImageView mImageView;
    private TextView mTextViewTitle;
    private TextView mTextViewIntro;
    private TextView mTextViewAction;
    private TextView mTextViewMaxNum;
    private TextView mTextViewWare;
    private RelativeLayout mRelativeLayoutMax;

    private Fragment mFragment;
    private TempletPresenter mPresenter;
    private BeanSubTempletInfo subTempletInfo;
    private int templetPosition;
    private long clickDelayTime = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public Db1View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Db1View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    /**
     * 构造
     *
     * @param context          context
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     */
    public Db1View(Context context, Fragment fragment, TempletPresenter templetPresenter) {
        this(context, null);
        mFragment = fragment;
        mPresenter = templetPresenter;
    }

    @Override
    public void onClick(View v) {
        long current = System.currentTimeMillis();
        if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
            int id = v.getId();
            if (id == R.id.textview_action) {
                if (subTempletInfo.action != null) {
                    mPresenter.getBookFromNet(subTempletInfo.action, subTempletInfo.id, new LoadBookListener() {

                        @Override
                        public void success(String status, String message, BeanBookInfo beanBookInfo) {
                            //status == 5
                            mSaleProgressView.setMax(subTempletInfo.limit);
                            mSaleProgressView.setProgress(subTempletInfo.hasGot + 1);
                            mTextViewAction.setEnabled(false);
                            if (mRelativeLayoutMax.getVisibility() == VISIBLE) {
                                int progress = (subTempletInfo.hasGot + 1) * 100 / subTempletInfo.limit;
                                String maxNum = getContext().getString(R.string.str_limitfree_num);
                                try {
                                    mTextViewMaxNum.setText(String.format(maxNum, progress + "%", subTempletInfo.limit));
                                } catch (Exception e) {
                                    ALog.printStackTrace(e);
                                }
                            }
                            mTextViewAction.setText(getContext().getResources().getString(R.string.already_received));
                            mPresenter.getBookSuccess(subTempletInfo);
                        }

                        @Override
                        public void fail(String status, String message) {
                            switch (status) {
                                case "4":
                                    break;
                                default://6，7
                                    ToastAlone.showShort(message);
                            }
                        }
                    });
                    mPresenter.logSingleBookClick(TempletPresenter.LOG_CLICK_ACTION_DB1, TempletPresenter.LOG_CLICK_GET_BOOK, subTempletInfo.id, templetPosition);
                }
            } else {
                if (!TextUtils.isEmpty(subTempletInfo.id)) {
                    mPresenter.skipToBookDetail(subTempletInfo.id, subTempletInfo.title);
                    mPresenter.logSingleBookClick(TempletPresenter.LOG_CLICK_ACTION_DB1, TempletPresenter.LOG_CLICK_BOOK_DETAIL, subTempletInfo.id, templetPosition);
                }
            }
        }
        clickDelayTime = current;
    }

    private void setListener() {
        mTextViewAction.setOnClickListener(this);
        setOnClickListener(this);
    }

    private void initData() {

    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        //        setOrientation(HORIZONTAL);
        int paddingTop = DimensionPixelUtil.dip2px(getContext(), 8);
        int paddingLeft = DimensionPixelUtil.dip2px(getContext(), 16);
        //        int paddingBottom = DimensionPixelUtil.dip2px(mContext, 5);
        setPadding(paddingLeft, paddingTop, paddingLeft, 0);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(getContext()).inflate(R.layout.view_db1, this);
        mTextViewDel = findViewById(R.id.textview_delline);
        mSaleProgressView = findViewById(R.id.progress_sale);
        mImageView = findViewById(R.id.imageview);
        mTextViewTitle = findViewById(R.id.textview_title);
        mTextViewIntro = findViewById(R.id.textview_intro);
        mTextViewAction = findViewById(R.id.textview_action);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewAction);
        mTextViewMaxNum = findViewById(R.id.textview_maxnum);
        mTextViewWare = findViewById(R.id.textview_ware);
        mRelativeLayoutMax = findViewById(R.id.relative_max);
    }


    /**
     * 绑定数据
     *
     * @param templetInfo      templetInfo
     * @param templetPosition1 templetPosition
     */
    public void bindData(BeanTempletInfo templetInfo, int templetPosition1) {
        this.templetPosition = templetPosition1;
        if (templetInfo != null) {

            BeanSubTempletInfo sub = templetInfo.items.get(0);
            if (sub != null) {
                subTempletInfo = sub;
                mTextViewTitle.setText(sub.title);
                mTextViewIntro.setText(StringUtil.delSpaceAndLn(sub.desc));
                String imgUrl = "";
                if (sub.imgUrl != null && sub.imgUrl.size() > 0) {
                    imgUrl = sub.imgUrl.get(0);
                    if (!TextUtils.isEmpty(imgUrl)) {
                        GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, imgUrl, 0);
                    }
                }
                mTextViewWare.setText(sub.warn);
                mTextViewDel.setText(sub.delLine);
                if (sub.limit <= 0) {
                    mRelativeLayoutMax.setVisibility(GONE);
                } else {
                    int prosress = sub.hasGot * 100 / sub.limit;
                    String maxNum = getContext().getString(R.string.str_limitfree_num);
                    try {
                        mTextViewMaxNum.setText(String.format(maxNum, prosress + "%", sub.limit));
                    } catch (Exception e) {
                        ALog.printStackTrace(e);
                    }
                    mSaleProgressView.setMax(sub.limit);
                    mSaleProgressView.setProgress(sub.hasGot);
                    mRelativeLayoutMax.setVisibility(VISIBLE);
                }
                if (sub.action != null) {
                    if (TempletContant.ACTION_TYPE_DISABLE.equals(sub.action.type)) {
                        mTextViewAction.setEnabled(false);
                    } else {
                        mTextViewAction.setEnabled(true);
                    }
                    mTextViewAction.setText(sub.action.title);
                }
            }
        }
    }


    /**
     * 清除图片
     */
    public void clearImageView() {
        if (mImageView != null) {
            Glide.with(mFragment).clear(mImageView);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, null, 0);
        }
    }
}
