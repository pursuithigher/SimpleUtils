package com.dzbook.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.ChaseRecommendPresenter;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.reader.BeanRecommentBookInfo;

/**
 * ChaseRecommendBookItemView
 *
 * @author lizhongzhong 2018/3/9.
 */

public class ChaseRecommendBookItemView extends RelativeLayout {

    private ImageView imageview;
    private TextView textviewChase;

    private BeanBookInfo bookSimpleBean;

    private ChaseRecommendPresenter mPresenter;

    private BeanRecommentBookInfo bean;

    /**
     * 构造
     *
     * @param context context
     */
    public ChaseRecommendBookItemView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ChaseRecommendBookItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public ChaseRecommendBookItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
        initData();
        setListener();
    }


    private void initView(AttributeSet attrs) {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_siglebook, this);

        imageview = view.findViewById(R.id.imageview);
        textviewChase = view.findViewById(R.id.textview_chase);
    }


    private void initData() {
        imageview.setImageResource(R.drawable.aa_default_icon);
        textviewChase.setText("");
    }

    private void setListener() {
        imageview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookSimpleBean != null && !TextUtils.isEmpty(bookSimpleBean.bookId) && mPresenter != null) {
                    mPresenter.bookDetailLauch(bookSimpleBean.bookId, bookSimpleBean.bookName);
                    mPresenter.logYdqZgTJ(false, bookSimpleBean.bookId, bean);
                }
            }
        });
    }

    /**
     * 绑定数据
     *
     * @param recommendPresenter recommendPresenter
     * @param beanBookInfo       beanBookInfo
     * @param recommendBean      recommendBean
     */
    public void bindData(ChaseRecommendPresenter recommendPresenter, BeanBookInfo beanBookInfo, BeanRecommentBookInfo recommendBean) {
        setPresenter(recommendPresenter);
        if (beanBookInfo != null) {
            this.bookSimpleBean = beanBookInfo;
            this.bean = recommendBean;
            textviewChase.setText(beanBookInfo.bookName);
            if (!TextUtils.isEmpty(beanBookInfo.coverWap)) {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), imageview, beanBookInfo.coverWap, R.drawable.aa_default_icon);
            }

        }
    }

    public void setPresenter(ChaseRecommendPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }

}
