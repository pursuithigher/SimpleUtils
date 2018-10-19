package com.dzbook.utils;

import android.content.res.Resources;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.bean.MainTabBean;
import com.dzbook.bean.MainTabBeanInfo;
import com.dzbook.fragment.MainTypeContentFragment;
import com.dzbook.fragment.main.MainPersonalFragment;
import com.dzbook.fragment.main.MainShelfFragment;
import com.dzbook.fragment.main.MainStoreFragment;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.ishugui.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TabInfoUtils
 *
 * @author lizhongzhong on 2018/01/16.
 */
public class MainTabInfoUtils {
    /**
     * TAB_PERSONAL_INDEX
     */
    public static final int TAB_PERSONAL_INDEX = 3;
    private static volatile MainTabInfoUtils sInstance;
    private static final String[] TABS = {"shelf", "store", "personal", "sort"};
    private static final int[] RESS = {R.drawable.ic_main_shelf, R.drawable.ic_main_store, R.drawable.ic_main_personal, R.drawable.ic_main_type};
    private static final String[] LOG_ID_S = {LogConstants.ZONE_MAIN_SJ, LogConstants.ZONE_MAIN_SC, LogConstants.ZONE_MAIN_WD, LogConstants.ZONE_MAIN_FL};
    private static final Class<?>[] CLASS_S = {MainShelfFragment.class, MainStoreFragment.class, MainPersonalFragment.class, MainTypeContentFragment.class};
    private static final int[] TITLES = {R.string.book_shelf, R.string.book_store, R.string.my, R.string.str_fl};

    private List<MainTabBean> list = new ArrayList<>();
    private int defaultEnterTab = 0;
    private int defaultExitTab = 0;

    /**
     * 获取实例
     *
     * @return 实例
     */
    public static MainTabInfoUtils getInstance() {
        if (sInstance == null) {
            synchronized (MainTabInfoUtils.class) {
                if (sInstance == null) {
                    sInstance = new MainTabInfoUtils();
                }
            }
        }
        return sInstance;
    }


    /**
     * 添加Tab
     */
    public void addMainTabs() {
        list.clear();
        try {
            String mainTabJson = SpUtil.getinstance(AppConst.getApp()).getString(SpUtil.DZ_KEY_MAIN_TAB_JSON);
            if (!TextUtils.isEmpty(mainTabJson)) {
                MainTabBeanInfo mainTabBeanInfo = new MainTabBeanInfo().parseJSON(new JSONObject(mainTabJson));

                if (mainTabBeanInfo != null && mainTabBeanInfo.mainTabBeans != null && mainTabBeanInfo.mainTabBeans.size() >= 3) {
                    List<MainTabBean> tabBeans = readyMainTabBeans(mainTabBeanInfo);
                    if (tabBeans.size() >= 3) {
                        addMainTabNetBeans(mainTabBeanInfo, tabBeans);
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        addDefaultMainTabs();
    }

    /**
     * 初始化以后的网络数据，添加到tab
     *
     * @param mainTabBeanInfo
     * @param tabBeans
     */
    private void addMainTabNetBeans(MainTabBeanInfo mainTabBeanInfo, List<MainTabBean> tabBeans) {
        list.addAll(tabBeans);
        for (int i = 0; i < list.size(); i++) {
            MainTabBean bean = list.get(i);
            bean.index = i;
            if (TextUtils.equals(bean.tab, mainTabBeanInfo.defaultEnter)) {
                defaultEnterTab = i;
            }
            if (TextUtils.equals(bean.tab, mainTabBeanInfo.defaultOut)) {
                defaultExitTab = i;
            }
        }

        addTabLog(false, list);
    }

    /**
     * 处理网络数据列表
     *
     * @param mainTabBeanInfo
     * @return
     */
    private List<MainTabBean> readyMainTabBeans(MainTabBeanInfo mainTabBeanInfo) {
        List<MainTabBean> tabBeans = new ArrayList<>();
        Resources resources = AppConst.getApp().getResources();
        for (int i = 0; i < mainTabBeanInfo.mainTabBeans.size(); i++) {
            MainTabBean bean = mainTabBeanInfo.mainTabBeans.get(i);
            if (bean != null && !TextUtils.isEmpty(bean.tab)) {
                for (int j = 0; j < TABS.length; j++) {
                    //书架，书城，发现，我的，免费，分类
                    if (TextUtils.equals(bean.tab, TABS[j])) {
                        bean.logId = LOG_ID_S[j];
                        bean.glcass = CLASS_S[j];
                        bean.res = RESS[j];

                        if (TextUtils.isEmpty(bean.title)) {
                            bean.title = resources.getString(TITLES[j]);
                        }
                        tabBeans.add(bean);
                    }
                }
            }
        }
        return tabBeans;
    }


    private void addDefaultMainTabs() {
        list.clear();
        defaultEnterTab = 1;
        defaultExitTab = 0;
        Resources resources = AppConst.getApp().getResources();
        MainTabBean shelfTabBean = new MainTabBean(0, TABS[0], LogConstants.ZONE_MAIN_SJ, MainShelfFragment.class, RESS[0], resources.getString(TITLES[0]));
        MainTabBean storeTabBean = new MainTabBean(1, TABS[1], LogConstants.ZONE_MAIN_SC, MainStoreFragment.class, RESS[1], resources.getString(TITLES[1]));
        MainTabBean typeTabBean = new MainTabBean(2, TABS[3], LogConstants.ZONE_MAIN_FL, MainTypeContentFragment.class, RESS[3], resources.getString(TITLES[3]));
        MainTabBean classTabBean = new MainTabBean(TAB_PERSONAL_INDEX, TABS[2], LogConstants.ZONE_MAIN_WD, MainPersonalFragment.class, RESS[2], resources.getString(TITLES[2]));

        list.add(shelfTabBean);
        list.add(storeTabBean);
        list.add(typeTabBean);
        list.add(classTabBean);

        addTabLog(true, list);
    }


    public List<MainTabBean> getList() {
        return list;
    }

    public int getDefaultEnterTab() {
        return defaultEnterTab;
    }

    public int getDefaultExitTab() {
        return defaultExitTab;
    }

    /**
     * 获取单Tab信息
     *
     * @param viewPosition viewPosition
     * @return MainTabBean
     */
    public MainTabBean getSingleTabInfo(int viewPosition) {
        if (viewPosition < list.size()) {
            return list.get(viewPosition);
        }
        return null;
    }


    /**
     * 通过type获取tab信息
     *
     * @param tabType tabType
     * @return MainTabBean
     */
    public MainTabBean getInfoByType(String tabType) {
        if (list.size() > 0) {
            for (MainTabBean sti : list) {
                if (sti != null && !TextUtils.isEmpty(sti.tab) && !TextUtils.isEmpty(tabType) && tabType.equals(sti.tab)) {
                    return sti;
                }
            }
        }
        return null;
    }

    /**
     * 打点
     *
     * @param isDefault
     * @param tabs
     */
    private void addTabLog(final boolean isDefault, final List<MainTabBean> tabs) {
        DzSchedulers.execute(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();
                map.put(LogConstants.KEY_TAB_IS_DEFAULT, isDefault ? "1" : "0");
                map.put(LogConstants.KEY_TABS, tabs.toString());
                DzLog.getInstance().logEvent(LogConstants.EVENT_MAIN_TAB, map, "");
            }
        });
    }

}
