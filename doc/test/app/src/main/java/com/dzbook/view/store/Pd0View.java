package com.dzbook.view.store;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.dzbook.adapter.SubTabFragmentPagerAdapter;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.MainStorePresenter;
import com.dzbook.templet.ChannelPageFragment;
import com.dzbook.templet.ChannelWebPageFragment;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import huawei.widget.HwSubTabWidget;
import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * Pd0View
 *
 * @author dongdianzhou on 2018/1/6.
 */

public class Pd0View extends LinearLayout {

    private HwSubTabWidget mHwSubTabWidget;
    private ViewPager mViewPager;
    private MainStorePresenter presenter;

    private List<BeanSubTempletInfo> channels;
    private String templetID = "";
    private String pageType = LogConstants.MODULE_NSC;

    private SubTabFragmentPagerAdapter mSubTabFragmentPagerAdapter;

    /**
     * 构造
     *
     * @param context context
     */
    public Pd0View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Pd0View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    public void setPresenter(MainStorePresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * pause
     */
    public void pause() {
        ALog.cmtDebug("pause");
        if (mSubTabFragmentPagerAdapter != null) {
            int count = mSubTabFragmentPagerAdapter.getCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    Fragment fragment = mSubTabFragmentPagerAdapter.getItem(i);
                    if (fragment != null && fragment instanceof ChannelPageFragment) {
                        ((ChannelPageFragment) fragment).pauseRecycleOnlyStopBn0();
                    } else if (fragment != null && fragment instanceof ChannelWebPageFragment) {
                        ((ChannelWebPageFragment) fragment).pauseRecycle();
                    }
                }
            }
        }
    }

    /**
     * resume
     */
    public void resume() {
        ALog.cmtDebug("resume");
        if (mSubTabFragmentPagerAdapter != null) {
            int count = mSubTabFragmentPagerAdapter.getCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    Fragment fragment = mSubTabFragmentPagerAdapter.getItem(i);
                    if (fragment != null && fragment instanceof ChannelPageFragment) {
                        ((ChannelPageFragment) fragment).resumeReferenceOnlyStopBn0();
                    } else if (fragment != null && fragment instanceof ChannelWebPageFragment) {
                        ((ChannelWebPageFragment) fragment).resumeReference();
                    }
                }
            }
        }
    }

    private void initView() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_pd0, this);
        mViewPager = findViewById(R.id.viewpager);
        mHwSubTabWidget = initializeSubTabs(getContext());
    }

    private void setListener() {
    }

    private void initData() {

    }

    private HwSubTabWidget initializeSubTabs(Context context) {
        HwSubTabWidget subTabWidget = findViewById(R.id.viewpagertab);
        mSubTabFragmentPagerAdapter = new SubTabFragmentPagerAdapter((FragmentActivity) context, mViewPager, subTabWidget);
        return subTabWidget;
    }

    /**
     * 绑定数据
     *
     * @param templetsInfo    templetsInfo
     * @param fragmentManager fragmentManager
     * @param pageType1       pageType
     * @param selectedID      selectedID
     * @param templetID1      templetID
     */
    public void bindData(FragmentManager fragmentManager, BeanTempletsInfo templetsInfo, String selectedID, String pageType1, String templetID1) {
        this.templetID = templetID1;
        this.pageType = pageType1;
        if (templetsInfo.isContainChannel()) {
            channels = templetsInfo.getValidChannels();
            for (int i = 0; i < channels.size(); i++) {
                BeanSubTempletInfo temp = channels.get(i);
                if (temp != null) {
                    if (!TextUtils.isEmpty(temp.title)) {
                        int position = 0;
                        //接口下发了
                        Bundle bundler = new Bundle();
                        if (temp.id.equals(templetsInfo.channelId)) {
                            bundler.putParcelable(TempletContant.KEY_CHANNEL_OBJECT, templetsInfo);
                        }
                        if (temp.id.equals(selectedID)) {
                            position = i;
                        }
                        bundler.putString(TempletContant.KEY_CHANNEL_TYPE, temp.type);
                        bundler.putString(TempletContant.KEY_CHANNEL_URL, temp.actionUrl);
                        bundler.putString(TempletContant.KEY_CHANNEL_ID, temp.id);
                        bundler.putString(TempletContant.KEY_CHANNEL_POSITION, String.valueOf(position));
                        bundler.putString(TempletContant.KEY_CHANNEL_SELECTED_ID, templetsInfo.channelId);
                        bundler.putString(TempletContant.KEY_CHANNEL_TITLE, temp.title);
                        bundler.putString(TempletContant.KEY_CHANNEL_TEMPLETID, templetID1);
                        bundler.putString(TempletContant.KEY_CHANNEL_PAGETYPE, LogConstants.MODULE_NSC);
                        boolean selected = i == position;
                        BaseFragment baseFragment = null;
                        if (!TextUtils.isEmpty(temp.actionUrl)) {
                            baseFragment = new ChannelWebPageFragment();
                        } else {
                            baseFragment = new ChannelPageFragment();
                        }
                        HwSubTabWidget.SubTab subTab = mHwSubTabWidget.newSubTab(temp.title);
                        mSubTabFragmentPagerAdapter.addSubTab(subTab, baseFragment, bundler, selected, channels, this.templetID, presenter, this.pageType);

                    }
                }
            }
        }
    }


    private List<String> getMeasureStrList(BeanTempletsInfo templetsInfo) {
        List<String> list = new ArrayList<>();
        List<BeanSubTempletInfo> channels1 = templetsInfo.getValidChannels();
        for (BeanSubTempletInfo info : channels1) {
            if (info != null) {
                list.add(info.title);
            }
        }
        return list;
    }

    /**
     * 返回顶部
     */
    public void returnViewTop() {
        ALog.cmtDebug("returnViewTop");
        int position = mViewPager.getCurrentItem();
        Fragment fragment = mSubTabFragmentPagerAdapter.getItem(position);
        if (fragment != null && fragment instanceof ChannelPageFragment) {
            ((ChannelPageFragment) fragment).returnTop();
        }
    }
}
