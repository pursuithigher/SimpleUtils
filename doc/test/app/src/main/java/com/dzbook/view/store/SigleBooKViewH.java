package com.dzbook.view.store;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * SigleBooKViewH
 *
 * @author dongdianzhou on 2018/1/9.
 */

public class SigleBooKViewH extends RelativeLayout {

    private TempletPresenter mTempletPresenter;
    private int templetPosition;
    private long clickDelayTime;
    private ImageView mImageView;
    private TextView mTextViewTitle;
    private TextView mTextViewIntro;
    private TextView mTextViewAuthor;
    private View viewLine;
    private BeanSubTempletInfo sub;
    private BeanTempletInfo templetInfo;
    private int logClickType = -10;

    /**
     * 构造
     *
     * @param context context
     */
    public SigleBooKViewH(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SigleBooKViewH(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    public void setTempletPresenter(TempletPresenter templetPresenter) {
        this.mTempletPresenter = templetPresenter;
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime > TempletContant.CLICK_DISTANSE && templetInfo != null && mTempletPresenter != null) {
                    mTempletPresenter.skipToBookDetail(sub.id, sub.title);
                    mTempletPresenter.logCommonBookClick(logClickType, TempletPresenter.LOG_CLICK_BOOK, templetInfo, sub.id, templetPosition);
                    mTempletPresenter.logHw(templetInfo, sub, templetPosition, logClickType, TempletPresenter.LOG_CLICK_BOOK, true);
                }
                clickDelayTime = current;
            }
        });
    }

    private void initData() {

    }

    private void initView() {

        setBackgroundResource(R.drawable.selector_hw_list_item);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 152));
        setLayoutParams(params);
        //        if (DeviceInfoUtils.getInstanse().getOsVersionInt() >= DeviceInfoUtils.NOT_SUPPORT_CONSTRAINT_LAYOUT_VERSION) {
        //            LayoutInflater.from(getContext()).inflate(R.layout.view_siglebookviewh, this);
        //        } else {
        //            LayoutInflater.from(getContext()).inflate(R.layout.view_siglebookviewh_lower, this);
        //        }
        //        setBackgroundResource(R.drawable.com_common_item_selector);
        LayoutInflater.from(getContext()).inflate(R.layout.item_bookstoretop2, this);
        mImageView = findViewById(R.id.ivBookIcon);
        mTextViewTitle = findViewById(R.id.tvBookName);
        mTextViewAuthor = findViewById(R.id.tvAuthorName);
        mTextViewIntro = findViewById(R.id.tvBookContent);
        final float num1 = 1.3f;
        final float num2 = 1.1f;
        TypefaceUtils.setLineSpacingBylocale(getContext(), mTextViewIntro, num1, num2);
        viewLine = findViewById(R.id.view_line);
        findViewById(R.id.tvOrderName).setVisibility(GONE);
    }

    /**
     * 绑定数据
     *
     * @param templetInfo1     templetInfo
     * @param subTempletInfo   subTempletInfo
     * @param logClickType1    logClickType
     * @param templetPosition1 templetPosition
     * @param isShowLine       isShowLine
     */
    public void bindData(BeanTempletInfo templetInfo1, BeanSubTempletInfo subTempletInfo, int logClickType1, int templetPosition1, boolean isShowLine) {
        this.templetPosition = templetPosition1;
        this.logClickType = logClickType1;
        sub = subTempletInfo;
        this.templetInfo = templetInfo1;
        if (isShowLine) {
            viewLine.setVisibility(VISIBLE);
        } else {
            viewLine.setVisibility(GONE);
        }
        mTextViewTitle.setText(subTempletInfo.title);
        mTextViewIntro.setText(subTempletInfo.desc);
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
     * 清除图片
     */
    public void clearImageView() {
        if (mImageView != null) {
            Glide.with(getContext()).clear(mImageView);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, null, 0);
        }
    }

    /**
     * 设置图片
     */
    public void referenceImageView() {
        if (mImageView != null && sub != null) {
            String imgUrl = "";
            if (sub.imgUrl != null && sub.imgUrl.size() > 0) {
                imgUrl = sub.imgUrl.get(0);
            }
            if (!TextUtils.isEmpty(imgUrl)) {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, imgUrl, 0);
            }
        }
    }
}
