package com.dzbook.view.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
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
 * Db0View
 *
 * @author dongdianzhou on 2018/1/6.
 */

public class Db0View extends ConstraintLayout {

    private Fragment mFragment;
    private TempletPresenter mPresenter;
    private long clickDelayTime = 0;
    private ImageView mImageView;
    private TextView mTextViewTitle;
    private TextView mTextViewIntro;
    private TextView mTextViewAuthor;
    private SigleBooKViewH siglebookview;
    private BeanSubTempletInfo subTempletInfo;
    private View mLineView;
    private int templetPosition;

    /**
     * 构造
     *
     * @param context context
     */
    public Db0View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Db0View(Context context, @Nullable AttributeSet attrs) {
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
    public Db0View(Context context, Fragment fragment, TempletPresenter templetPresenter) {
        this(context, null);
        mFragment = fragment;
        mPresenter = templetPresenter;
    }

    private void setListener() {
        siglebookview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime > TempletContant.CLICK_DISTANSE && subTempletInfo != null) {
                    mPresenter.skipToBookDetail(subTempletInfo.id, subTempletInfo.title);
                    mPresenter.logSingleBookClick(TempletPresenter.LOG_CLICK_ACTION_DB0, TempletPresenter.LOG_CLICK_BOOK_DETAIL, subTempletInfo.id, templetPosition);
                }
                clickDelayTime = current;
            }
        });
    }

    private void initData() {

    }

    private void initView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        LayoutInflater.from(getContext()).inflate(R.layout.view_db0, this);


        mImageView = findViewById(R.id.ivBookIcon);
        mTextViewTitle = findViewById(R.id.tvBookName);
        mTextViewAuthor = findViewById(R.id.tvAuthorName);
        mTextViewIntro = findViewById(R.id.tvBookContent);
        //        mTextViewIntro.setMaxLines(3);
        siglebookview = findViewById(R.id.siglebookview);
        mLineView = findViewById(R.id.view_line);
    }

    /**
     * 绑定数据
     *
     * @param templetInfo      templetInfo
     * @param templetPosition1 templetPosition1
     * @param position  position
     */
    public void bindData(BeanTempletInfo templetInfo, int templetPosition1, int position) {
        this.templetPosition = templetPosition1;
        if (templetInfo != null) {
            BeanSubTempletInfo sub = templetInfo.items.get(0);
            if (sub != null) {
                subTempletInfo = sub;
                mTextViewTitle.setText(sub.title);
                mTextViewIntro.setText(sub.desc);
                mTextViewAuthor.setText(sub.author);
                String imgUrl = "";
                if (sub.imgUrl != null && sub.imgUrl.size() > 0) {
                    imgUrl = sub.imgUrl.get(0);
                    if (!TextUtils.isEmpty(imgUrl)) {
                        GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mImageView, imgUrl, 0);
                    }
                }
            }
            if (position == templetInfo.items.size() - 1) {
                mLineView.setVisibility(GONE);
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
