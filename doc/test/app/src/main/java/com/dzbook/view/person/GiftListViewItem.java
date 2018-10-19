package com.dzbook.view.person;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ViewUtils;
import com.ishugui.R;

import hw.sdk.net.bean.gift.GiftListBean;

/**
 * 礼物列表item
 *
 * @author KongXP on 2018/4/25.
 */

public class GiftListViewItem extends RelativeLayout {
    private RelativeLayout mRlIcon;
    private TextView mTvIcon;
    private ImageView mimgicon;
    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvInfo;
    private Context mContext;
    private LinearLayout mLlGiftListItem;
    private RelativeLayout mRlInfoBg;
    private GiftListBean jumpBeanInfo;
    private View vLine;

    /**
     * 构造
     *
     * @param context context
     */
    public GiftListViewItem(Context context) {
        this(context, null);
        mContext = context;
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public GiftListViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
        mContext = context;
    }

    private void setListener() {

    }

    private void initData() {

    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_gift_list_item, this);

        mRlIcon = view.findViewById(R.id.rl_icon);
        mTvIcon = view.findViewById(R.id.tv_icon);
        mimgicon = view.findViewById(R.id.img_icon);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvTime = view.findViewById(R.id.tv_time);
        mTvInfo = view.findViewById(R.id.tv_info);
        mRlInfoBg = view.findViewById(R.id.rl_info_bg);
        mLlGiftListItem = view.findViewById(R.id.ll_gift_list_item);
        vLine = view.findViewById(R.id.v_line);
    }

    /**
     * 绑定数据
     *
     * @param giftListBean  giftListBean
     * @param isShowEndLine isShowEndLine
     */
    public void bindData(GiftListBean giftListBean, boolean isShowEndLine) {
        int roundRadius = DimensionPixelUtil.dip2px(getContext(), 3);
        if (giftListBean != null) {
            jumpBeanInfo = giftListBean;
            if (!giftListBean.imgUrl.isEmpty()) {
                mRlIcon.setVisibility(View.GONE);
                mimgicon.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(giftListBean.imgUrl).into(mimgicon);
            } else {
                mimgicon.setVisibility(View.GONE);
                mRlIcon.setVisibility(View.VISIBLE);
                if (!giftListBean.imgColor.isEmpty()) {
                    GradientDrawable gradientDrawables = ViewUtils.getGradientDrawable(giftListBean.imgColor, roundRadius);
                    CompatUtils.setBackgroundDrawable(mRlIcon, gradientDrawables);
                } else {
                    GradientDrawable gradientDrawables = ViewUtils.getGradientDrawable(String.valueOf(CompatUtils.getColor(getContext(), R.color.color_100_CD2325)), roundRadius);
                    CompatUtils.setBackgroundDrawable(mRlIcon, gradientDrawables);
                }
                mTvIcon.setText(giftListBean.imgName);
            }
            mTvTitle.setText(giftListBean.title);
            mTvTime.setText(giftListBean.time);
            mTvInfo.setText(giftListBean.desc);
            mRlInfoBg.setBackgroundResource(R.color.transparent);
            mTvInfo.setTextColor(CompatUtils.getColor(getContext(), R.color.color_b5b5b5));
            mLlGiftListItem.setOnClickListener(null);
            if (GiftListBean.CONSTANT_TWO == giftListBean.type) {
                if (GiftListBean.CONSTANT_ZERO == giftListBean.status) {
                    mRlInfoBg.setBackgroundResource(R.drawable.selector_hw_btn_common1);
                    mTvInfo.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_000000));
                    mLlGiftListItem.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jumpToH5(jumpBeanInfo);
                        }
                    });
                }
            }
        }
        if (null != vLine) {
            if (!isShowEndLine) {
                vLine.setVisibility(View.VISIBLE);
            } else {
                vLine.setVisibility(View.GONE);
            }
        }
    }

    private void jumpToH5(GiftListBean jumpBeanInfo1) {
        CenterDetailActivity.show(getContext(), RequestCall.urlObjectToReceive() + String.valueOf(jumpBeanInfo1.gid), "");
    }
}
