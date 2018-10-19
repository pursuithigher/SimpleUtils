package com.dzbook.view.vip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ishugui.R;

import hw.sdk.net.bean.vip.VipContinueOpenHisBean;

/**
 * 自动续订 View
 *
 * @author KongXP on 2018/4/29.
 */
public class AutoOrderVipView extends RelativeLayout {

    private TextView mTvVipDes;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private TextView mTvOrderNum;
    private TextView mTvCost;
    private Context mContext;


    /**
     * 构造
     *
     * @param context context
     */
    public AutoOrderVipView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public AutoOrderVipView(Context context, AttributeSet attrs) {
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_aoto_order_vip_item, this);

        mTvVipDes = view.findViewById(R.id.tv_vip_des);
        mTvStartTime = view.findViewById(R.id.tv_start_time);
        mTvEndTime = view.findViewById(R.id.tv_end_time);
        mTvOrderNum = view.findViewById(R.id.tv_order_num);
        mTvCost = view.findViewById(R.id.tv_cost);
    }


    /**
     * 绑定数据
     *
     * @param recordBeanInfo bean
     */
    @SuppressLint("SetTextI18n")
    public void bindData(VipContinueOpenHisBean recordBeanInfo) {
        if (recordBeanInfo != null) {
            mTvVipDes.setText(recordBeanInfo.vipOpenDes);
            mTvStartTime.setText(mContext.getResources().getString(R.string.str_member_start_time) + recordBeanInfo.memberStartTime);
            mTvEndTime.setText(mContext.getResources().getString(R.string.str_member_end_time) + recordBeanInfo.memberEndTime);
            mTvOrderNum.setText(mContext.getResources().getString(R.string.str_order_num) + recordBeanInfo.orderNum);
            mTvCost.setText(recordBeanInfo.cost);
        }
    }
}
