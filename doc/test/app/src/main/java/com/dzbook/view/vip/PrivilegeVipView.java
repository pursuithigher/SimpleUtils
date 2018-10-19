package com.dzbook.view.vip;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.vip.VipPrivilegeActivity;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;


/**
 * 特权
 *
 * @author gavin
 */
public class PrivilegeVipView extends LinearLayout {
    private Context mContext;
    private TextView mTvOpen;

    /**
     * 构造
     *
     * @param context context
     */
    public PrivilegeVipView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PrivilegeVipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        mTvOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VipPrivilegeActivity.launch((Activity) mContext);
            }
        });
    }

    private void initData() {

    }

    private void initView() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        setLayoutParams(params);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_vip_privilege, this);
        mTvOpen = view.findViewById(R.id.ll_vip_detail);
        TextView mTvPrivilege = view.findViewById(R.id.tv_vip_privilege);
        TypefaceUtils.setHwChineseMediumFonts(mTvPrivilege);
        TypefaceUtils.setHwChineseMediumFonts(mTvOpen);
    }

}