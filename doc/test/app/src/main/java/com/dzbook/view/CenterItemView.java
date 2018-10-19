package com.dzbook.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.ActivityCenterActivity;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.HashMap;

import hw.sdk.net.bean.ActivityCenterBean;

/**
 * 活动中心item
 *
 * @author gavin 2018/4/25
 */

public class CenterItemView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTextViewTitle;
    private TextView mTextViewTime;

    private ActivityCenterBean.CenterInfoBean mBean;

    private Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public CenterItemView(Context context) {
        super(context);
        mContext = context;
        initView();
        setListener();
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CenterDetailActivity.class);
                intent.putExtra("notiTitle", "");
                intent.putExtra("url", mBean.getUrl());
                HashMap<String, String> priMap = new HashMap<>();
                priMap.put("type", "1");
                intent.putExtra("web", "1008");
                intent.putExtra("priMap", priMap);
                intent.putExtra(LogConstants.KEY_OPERATE_FROM, ActivityCenterActivity.TAG);
                intent.putExtra(LogConstants.KEY_PART_FROM, LogConstants.RECHARGE_SOURCE_FROM_VALUE_7);
                mContext.startActivity(intent);
                BaseActivity.showActivity(mContext);
            }
        });
    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_activityitem, this);
        mImageView = rootView.findViewById(R.id.iv_acitem_icon);
        mTextViewTime = rootView.findViewById(R.id.tv_acitem_time);
        mTextViewTitle = rootView.findViewById(R.id.tv_acitem_title);
    }

    /**
     * 绑定数据
     *
     * @param bean bean
     */
    public void bindData(ActivityCenterBean.CenterInfoBean bean) {
        mBean = bean;
        GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(mContext, mImageView, bean.getImg(), R.drawable.aa_default_icon);
        mTextViewTitle.setText(bean.getTitle());
        mTextViewTime.setText(bean.getTemp());
        //        GradientDrawable myGrad = (GradientDrawable) mRelativeLayout.getBackground();
        //        myGrad.setColor(Color.parseColor(bean.getBgColor()));
    }
}
