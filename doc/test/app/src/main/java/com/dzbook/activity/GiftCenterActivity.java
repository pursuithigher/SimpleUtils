package com.dzbook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.adapter.SubTabGiftFragmentAdapter;
import com.dzbook.fragment.GiftExchangeFragment;
import com.dzbook.fragment.GiftReceiveFragment;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.ViewUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.ishugui.R;

import huawei.widget.HwSubTabWidget;

/**
 * 礼品中心
 *
 * @author KongXP on 18/4/20.
 */
public class GiftCenterActivity extends BaseSwipeBackActivity {
    private static final String TAG = "GiftCenterActivity";
    private ViewPager mViewPager;
    private DianZhongCommonTitle mCommonTitle;
    private HwSubTabWidget mHwSubTabWidget;
    private SubTabGiftFragmentAdapter mSubTabGiftFragmentAdapter;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_gift_center);
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_content);
        mHwSubTabWidget = initializeSubTabs(getContext());
        mCommonTitle = findViewById(R.id.commontitle);
    }

    private HwSubTabWidget initializeSubTabs(Context context) {
        HwSubTabWidget subTabWidget = findViewById(R.id.viewpagertab);
        mSubTabGiftFragmentAdapter = new SubTabGiftFragmentAdapter((FragmentActivity) context, mViewPager, subTabWidget);
        return subTabWidget;
    }

    @Override
    protected void initData() {
        setSwipeBackEnable(false);

        DzLog.getInstance().logPv(LogConstants.PV_LPDH, null, null);

        HwSubTabWidget.SubTab subTabGiftExchange = mHwSubTabWidget.newSubTab(getResources().getString(R.string.str_gift_exchange));
        GiftExchangeFragment giftExchangeFragment = new GiftExchangeFragment();

        HwSubTabWidget.SubTab subTabGiftReceive = mHwSubTabWidget.newSubTab(getResources().getString(R.string.my_gift));
        GiftReceiveFragment giftReceiveFragment = new GiftReceiveFragment();

        Intent intent = getIntent();
        String index;
        boolean first = true;
        if (null != intent && !TextUtils.isEmpty(index = intent.getStringExtra("index"))) {
            first = "1".equals(index);
        }
        mSubTabGiftFragmentAdapter.addSubTab(subTabGiftExchange, giftExchangeFragment, null, first);
        mSubTabGiftFragmentAdapter.addSubTab(subTabGiftReceive, giftReceiveFragment, null, !first);
    }

    @Override
    protected void setListener() {

        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideInputKeyboard(GiftCenterActivity.this, TAG);
                finish();
            }
        });
    }
}
