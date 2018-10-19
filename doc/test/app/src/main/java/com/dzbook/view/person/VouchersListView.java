package com.dzbook.view.person;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.vouchers.VouchersListBean;

/**
 * 代金券列表
 *
 * @author KongXP on 2018/4/25.
 */

public class VouchersListView extends RelativeLayout {
    private TextView mTvRemain;
    private TextView mTvAmount;
    private TextView mTvInfo;
    private TextView mTvTime;
    private ImageView mImgAngle;
    private LinearLayout mLlState;
    private Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public VouchersListView(Context context) {
        this(context, null);
        mContext = context;
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public VouchersListView(Context context, AttributeSet attrs) {
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_vouchers_list_item, this);
        mTvRemain = view.findViewById(R.id.tv_remain);
        mTvAmount = view.findViewById(R.id.tv_amount);
        mTvInfo = view.findViewById(R.id.tv_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mImgAngle = view.findViewById(R.id.img_angle);
        mLlState = view.findViewById(R.id.ll_state);
        TypefaceUtils.setHwChineseMediumFonts(mTvRemain);
    }

    /**
     * 并定数据
     *
     * @param vouchersListBean vouchersListBean
     */
    public void bindData(VouchersListBean vouchersListBean) {
        if (vouchersListBean != null) {
            /**
             *  0-正常，1-快过期，2-已使用，3-已过期
             */
            LayoutParams params = (LayoutParams) mImgAngle.getLayoutParams();
            switch (vouchersListBean.status) {
                case 0:
                    mImgAngle.setVisibility(View.GONE);
                    mLlState.setBackgroundResource(R.drawable.item_vouchers_bg_red);
                    break;
                case 1:
                    mImgAngle.setVisibility(View.VISIBLE);
                    params.height = DimensionPixelUtil.dip2px(mContext, 39);
                    params.width = DimensionPixelUtil.dip2px(mContext, 39);
                    mImgAngle.setLayoutParams(params);
                    mImgAngle.setBackgroundResource(R.drawable.item_vouchers_angle_expiring);
                    mLlState.setBackgroundResource(R.drawable.item_vouchers_bg_red);
                    break;
                case 2:
                    mImgAngle.setVisibility(View.VISIBLE);
                    params.height = DimensionPixelUtil.dip2px(mContext, 48);
                    params.width = DimensionPixelUtil.dip2px(mContext, 48);
                    mImgAngle.setLayoutParams(params);
                    mImgAngle.setBackgroundResource(R.drawable.item_vouchers_angle_use);
                    mLlState.setBackgroundResource(R.drawable.item_vouchers_bg_gray);
                    break;
                case 3:
                    mImgAngle.setVisibility(View.VISIBLE);
                    params.height = DimensionPixelUtil.dip2px(mContext, 48);
                    params.width = DimensionPixelUtil.dip2px(mContext, 48);
                    mImgAngle.setLayoutParams(params);
                    mImgAngle.setBackgroundResource(R.drawable.item_vouchers_angle_overdue);
                    mLlState.setBackgroundResource(R.drawable.item_vouchers_bg_gray);
                    break;
                default:
                    break;
            }
            setInfo(vouchersListBean);
        }
    }

    private void setInfo(VouchersListBean vouchersListBean) {
        if (!vouchersListBean.remain.isEmpty()) {
            mTvRemain.setText(vouchersListBean.remain);
        }
        if (!vouchersListBean.amount.isEmpty()) {
            mTvAmount.setText(vouchersListBean.amount);
        }
        if (!vouchersListBean.des.isEmpty()) {
            mTvInfo.setText(vouchersListBean.des);
        }
        if (!vouchersListBean.endTime.isEmpty()) {
            mTvTime.setText(vouchersListBean.endTime);
        }
    }
}
