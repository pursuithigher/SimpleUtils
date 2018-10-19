package com.dzbook.activity.vip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.activity.account.ConsumeSecondActivity;
import com.dzbook.mvp.UI.VipOpenSuccessUi;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzpay.recharge.netbean.VipOrdersResultBean;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.io.Serializable;

/**
 * VipOpenSuccessActivity
 *
 * @author lizz 2018/4/21.
 */

public class VipOpenSuccessActivity extends BaseSwipeBackActivity implements VipOpenSuccessUi {

    private DianZhongCommonTitle mCommontitle;

    private TextView tvOpenSuccessTips, tvTvUserAccount, tvOpenDay, tvMemberDeadline;

    private Button btBackVip, btLookOpenHis;

    /**
     * 跳转
     *
     * @param activity        activity
     * @param orderResultJson orderResultJson
     */
    public static void launch(Activity activity, VipOrdersResultBean orderResultJson) {
        Intent intent = new Intent(activity, VipOpenSuccessActivity.class);
        intent.putExtra(VipOrdersResultBean.VIP_ORDER_RESULT, orderResultJson);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return "VipOpenSuccessActivity";
    }


    @Override
    protected void initView() {
        mCommontitle = findViewById(R.id.commontitle);
        tvOpenSuccessTips = findViewById(R.id.tv_open_success_tips);
        tvTvUserAccount = findViewById(R.id.tv_tv_user_account);
        tvOpenDay = findViewById(R.id.tv_open_day);
        btBackVip = findViewById(R.id.bt_back_vip);
        btLookOpenHis = findViewById(R.id.bt_look_open_his);
        tvMemberDeadline = findViewById(R.id.tv_member_deadLine);
        TypefaceUtils.setHwChineseMediumFonts(tvOpenSuccessTips);
        TypefaceUtils.setHwChineseMediumFonts(tvTvUserAccount);
        TypefaceUtils.setHwChineseMediumFonts(tvOpenDay);
        TypefaceUtils.setHwChineseMediumFonts(tvMemberDeadline);
        TypefaceUtils.setHwChineseMediumFonts(btBackVip);
        TypefaceUtils.setHwChineseMediumFonts(btLookOpenHis);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_vip_open_success);
    }


    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Serializable serializable = intent.getSerializableExtra(VipOrdersResultBean.VIP_ORDER_RESULT);
        if (null != serializable && serializable instanceof VipOrdersResultBean) {
            VipOrdersResultBean bean = (VipOrdersResultBean) serializable;
            tvOpenSuccessTips.setText(bean.openMsg);
            tvTvUserAccount.setText(bean.nickName);
            tvOpenDay.setText(bean.openDays);
            tvMemberDeadline.setText(bean.deadLine);
        } else {
            finish();
        }
    }

    @Override
    protected void setListener() {

        mCommontitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btBackVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btLookOpenHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsumeSecondActivity.launch(getActivity(), "", "3");
            }
        });

    }


}
