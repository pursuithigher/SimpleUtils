package com.dzbook.activity.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.adapter.SubTabGiftFragmentAdapter;
import com.dzbook.fragment.ConsumeBookFirstFragment;
import com.dzbook.fragment.ConsumeBookSecondFragment;
import com.dzbook.view.DianZhongCommonTitle;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import huawei.widget.HwSubTabWidget;

/**
 * 消费记录
 *
 * @author lizz 2018/4/20.
 */
public class ConsumeBookSumActivity extends BaseSwipeBackActivity {

    private static final String TAG = "ConsumeBookSumActivity";
    private DianZhongCommonTitle mCommonTitle;

    private ViewPager mViewPager;
    private HwSubTabWidget mHwSubTabWidget;
    private SubTabGiftFragmentAdapter mSubTabGiftFragmentAdapter;

    /**
     * 打开
     *
     * @param activity activity
     */
    public static void launchConsumeBookSum(Activity activity) {
        Intent intent = new Intent(activity, ConsumeBookSumActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_consume_book_summary);
    }

    @Override
    protected void initData() {
        setSwipeBackEnable(false);

        HwSubTabWidget.SubTab subTabGiftExchange = mHwSubTabWidget.newSubTab(getResources().getString(R.string.str_book));
        ConsumeBookFirstFragment firstFragment = new ConsumeBookFirstFragment();

        HwSubTabWidget.SubTab subTabGiftReceive = mHwSubTabWidget.newSubTab(getResources().getString(R.string.str_other));
        ConsumeBookSecondFragment secondFragment = new ConsumeBookSecondFragment();

        Intent intent = getIntent();
        String index;
        boolean first = true;
        if (null != intent && !TextUtils.isEmpty(index = intent.getStringExtra("index"))) {
            first = "1".equals(index);
        }
        mSubTabGiftFragmentAdapter.addSubTab(subTabGiftExchange, firstFragment, null, first);
        mSubTabGiftFragmentAdapter.addSubTab(subTabGiftReceive, secondFragment, null, !first);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewById(R.id.commontitle);
        mViewPager = findViewById(R.id.vp_content);
        mHwSubTabWidget = initializeSubTabs(getContext());
    }

    private HwSubTabWidget initializeSubTabs(Context context) {
        HwSubTabWidget subTabWidget = findViewById(R.id.viewpagertab);
        mSubTabGiftFragmentAdapter = new SubTabGiftFragmentAdapter((FragmentActivity) context, mViewPager, subTabWidget);
        return subTabWidget;
    }

    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
