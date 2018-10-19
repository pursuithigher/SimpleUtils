package com.dzbook.view.store;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.adapter.SubTabFragmentPagerAdapter;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.LimitFreeTwoLevelPresenter;
import com.dzbook.templet.ChannelPageFragment;
import com.dzbook.templet.ChannelWebPageFragment;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.MeasureUtils;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import huawei.widget.HwSubTabWidget;
import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * Pd1View
 *
 * @author dongdianzhou on 2018/1/6.
 */
public class Pd1View extends LinearLayout {

    private Context mContext;
    private ViewPager mViewPager;
    private ImageView imageview;
    private SubTabFragmentPagerAdapter mSubTabFragmentPagerAdapter;
    private HwSubTabWidget mHwSubTabWidget;
    private List<BeanSubTempletInfo> channels;
    private String templetID = "";
    private String pageType = LogConstants.MODULE_NSCXMZYM;

    /**
     * 构造
     *
     * @param context context
     */
    public Pd1View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Pd1View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    /**
     * setPresenter
     *
     * @param presenter presenter
     */
    public void setPresenter(LimitFreeTwoLevelPresenter presenter) {
    }

    private void setListener() {
    }


    private void initData() {

    }

    private void initView() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_pd1, this);
        mViewPager = findViewById(R.id.viewpager_pd1);
        mHwSubTabWidget = initializeSubTabs(getContext());
        imageview = findViewById(R.id.imageview);
    }

    private HwSubTabWidget initializeSubTabs(Context context) {
        HwSubTabWidget subTabWidget = findViewById(R.id.tablayout_pd1);
        mSubTabFragmentPagerAdapter = new SubTabFragmentPagerAdapter((FragmentActivity) context, mViewPager, subTabWidget);
        return subTabWidget;
    }

    /**
     * 绑定数据
     *
     * @param templetsInfo ：频道item对应的数据
     * @param selectedID   ：选中tabid
     * @param pageType1    ：页面类型
     * @param templetID1   ：栏目id（二级列表必须）
     */
    public void bindData(BeanTempletsInfo templetsInfo, String selectedID, String pageType1, String templetID1) {
        bindDataPre(templetsInfo, pageType1, templetID1);
        if (templetsInfo.isContainChannel()) {
            channels = templetsInfo.getValidChannels();
            for (int i = 0; i < channels.size(); i++) {
                BeanSubTempletInfo temp = channels.get(i);
                if (temp != null) {
                    if (!TextUtils.isEmpty(temp.title)) {
                        int position = 0;
                        //接口下发了
                        Bundle bundler = new Bundle();
                        position = initBundleAndGetPosition(templetsInfo, selectedID, templetID1, i, temp, position, bundler);
                        boolean selected = i == position;
                        BaseFragment baseFragment = null;
                        if (!TextUtils.isEmpty(temp.actionUrl)) {
                            baseFragment = new ChannelWebPageFragment();
                        } else {
                            baseFragment = new ChannelPageFragment();
                        }
                        HwSubTabWidget.SubTab subTab = mHwSubTabWidget.newSubTab(temp.title);
                        mSubTabFragmentPagerAdapter.addSubTab(subTab, baseFragment, bundler, selected, channels, this.templetID, null, this.pageType);

                    }
                }
            }
        }
    }

    private int initBundleAndGetPosition(BeanTempletsInfo templetsInfo, String selectedID, String templetID1, int i, BeanSubTempletInfo temp, int position, Bundle bundler) {
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
        bundler.putString(TempletContant.KEY_CHANNEL_PAGETYPE, LogConstants.MODULE_NSCXMZYM);
        return position;
    }

    private void bindDataPre(BeanTempletsInfo templetsInfo, String pageType1, String templetID1) {
        this.templetID = templetID1;
        this.pageType = pageType1;
        List<String> measureStrList = getMeasureStrList(templetsInfo);
        final TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_pd0_text, null);
        int leftPadding = DimensionPixelUtil.dip2px(mContext, 16);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int viewDis = DimensionPixelUtil.dip2px(mContext, 24);
        int minWidth = DimensionPixelUtil.dip2px(mContext, 48);
        int paddingLeft = MeasureUtils.measureTabPadding(measureStrList, textView, viewDis, leftPadding, screenWidth, minWidth);

        if (paddingLeft >= leftPadding) {
            if (imageview.getVisibility() == VISIBLE) {
                imageview.setVisibility(GONE);
            }
        } else {
            if (imageview.getVisibility() == GONE) {
                imageview.setVisibility(VISIBLE);
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

}
