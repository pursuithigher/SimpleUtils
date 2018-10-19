package com.dzbook.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DzFragmentTabHost;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.dzbook.AppConst;
import com.dzbook.BaseLoadActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.activity.search.SearchActivity;
import com.dzbook.bean.MainTabBean;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.fragment.main.MainPersonalFragment;
import com.dzbook.fragment.main.MainShelfFragment;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.model.UserGrow;
import com.dzbook.mvp.UI.MainUI;
import com.dzbook.mvp.presenter.MainPresenter;
import com.dzbook.service.HwIntentService;
import com.dzbook.utils.MainTabInfoUtils;
import com.dzbook.utils.hw.CheckUpdateUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.utils.hw.PermissionUtils;
import com.dzbook.view.BookView;
import com.dzbook.view.NavigationLinearLayout;
import com.dzbook.view.navigation.BottomBarLayout;
import com.dzbook.view.shelf.ShelfManagerBottomView;
import com.dzbook.web.ActionEngine;
import com.dzbook.web.WebManager;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.List;

/**
 * 主页面。
 *
 * @author wxliao on 17/3/29.
 */
public class Main2Activity extends BaseLoadActivity implements MainUI, PermissionUtils.OnPermissionListener {
    /**
     * tag
     */
    public static final String TAG = "Main2Activity";
    private static final String TAB_INDEX = "tab_index";

    private DzFragmentTabHost fragmentTabHost;
    private BottomBarLayout bottomBarLayout;
    private NavigationLinearLayout mLinearLayout;

    private int selectTab = 0;
    private long exitTime = 0;

    private MainPresenter mPresenter;

    private PermissionUtils checkPermission;
    private ShelfManagerBottomView mShelfManagerBottomView;

    /**
     * 获取 mShelfManagerBottomView
     *
     * @return view
     */
    public ShelfManagerBottomView getShelfManagerBottomView() {
        return mShelfManagerBottomView;
    }

    private void dealIntent(Intent intent) {
        if (null != intent) {
            int turnPage = intent.getIntExtra("turnPage", -1);
            ALog.cmtDebug("turnPage:" + turnPage);
            switch (turnPage) {
                case ModelAction.TO_SEARCH:
                    SearchActivity.launch(this);
                    break;
                case ModelAction.TO_SIGN:
                    ActionEngine.getInstance().toSign(this);
                    break;
                case ModelAction.TO_BOOKSTORE:
                    selectTab = 1;
                    setBookStoreTableHost(selectTab);
                    break;
                case ModelAction.TO_READER:
                    ReaderUtils.continueReadBook(this);
                    break;
                default:
                    break;
            }
            int select = intent.getIntExtra("selectTab", -1);
            if (select > -1 && select < WebManager.getTabs().size()) {
                this.selectTab = select;
                setBookStoreTableHost(select);
            }
        }
    }

    /**
     * 启动
     *
     * @param context context
     * @param tab     tab
     */
    public static void launch(Context context, int tab) {
        Intent intent = new Intent(context, Main2Activity.class);
        intent.putExtra("selectTab", tab);
        context.startActivity(intent);
    }

    @Override
    public String getTagName() {
        return TAG;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ALog.cmtDebug("onNewIntent");
        dealIntent(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        mPresenter = new MainPresenter(this);
        setContentView(R.layout.ac_main);
        dealIntent(getIntent());

        DzSchedulers.mainDelay(new Runnable() {
            @Override
            public void run() {
                mPresenter.registerPushMsgListener();
                //后台检测更新
                CheckUpdateUtils.checkUpdate(getContext(), CheckUpdateUtils.BACKGROUND_UPDATE);
            }
        }, 1000);

        checkPermission = new PermissionUtils();
        UserGrow.synchrodataServerReaderTime(true);

        EventBusUtils.registerSticky(this);
        EventBusUtils.sendMessage(EventConstant.FINISH_SPLASH);
        mPresenter.registerHwAccountChangeReceiver();
        startService(new Intent(getApplicationContext(), HwIntentService.class));
    }


    @Override
    protected void initView() {
        mLinearLayout = findViewById(R.id.layout_navigationContainer);
        MainTabInfoUtils.getInstance().addMainTabs();

        mShelfManagerBottomView = findViewById(R.id.shelfmanagerbottomview);
        fragmentTabHost = findViewById(R.id.fragmentTabHost);
        bottomBarLayout = findViewById(R.id.bottomBarLayout);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        fragmentTabHost.getTabWidget().setDividerDrawable(null);
        List<MainTabBean> list = MainTabInfoUtils.getInstance().getList();
        mLinearLayout.addChildView(list);
        for (MainTabBean sti : list) {
            if (sti != null) {
                fragmentTabHost.addTab(fragmentTabHost.newTabSpec(sti.tab).setIndicator(sti.tab), sti.glcass, null);
            }
        }
        selectTab = MainTabInfoUtils.getInstance().getDefaultEnterTab();
        fragmentTabHost.setCurrentTab(selectTab);
        bottomBarLayout.post(new Runnable() {
            @Override
            public void run() {
                bottomBarLayout.setSelect(selectTab);
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.resumeSatartConfig(getIntent());
    }

    @Override
    protected void setListener() {
        bottomBarLayout.setNavigationListener(new BottomBarLayout.NavigationListener() {
            @Override
            public void onTabSelect(View view, int viewPosition, int selectedPosition) {
                fragmentTabHost.setCurrentTab(viewPosition);
                selectTab = viewPosition;
            }

            @Override
            public void onTabClick(View view, int viewPosition, int selectedPosition) {
                MainTabBean sti = MainTabInfoUtils.getInstance().getSingleTabInfo(viewPosition);
                if (sti != null) {
                    DzLog.getInstance().logClick(LogConstants.MODULE_MAIN, sti.logId, (viewPosition == selectedPosition) ? "2" : "1", null, null);
                }
            }

            @Override
            public void onReClick(View view, int viewPosition) {
//                Fragment currentFragment = getCurrentFragment();
//                if (currentFragment != null && currentFragment instanceof BaseFragment) {
//                    ((BaseFragment) currentFragment).onRefreshFragment();
//                }

            }

            @Override
            public void onDoubleClick(View view, int viewPosition) {
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment != null && currentFragment instanceof BaseFragment) {
                    ((BaseFragment) currentFragment).onRefreshFragment();
                }
            }

        });
    }

    @Override
    protected boolean isNoFragmentCache() {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_INDEX, selectTab);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        @SuppressLint("RestrictedApi") List<Fragment> list = getSupportFragmentManager().getFragments();
        if (list != null && list.size() > 0) {
            for (Fragment fragment : list) {
                if (fragment != null && fragment instanceof MainPersonalFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectTab = savedInstanceState.getInt(TAB_INDEX, MainTabInfoUtils.getInstance().getDefaultEnterTab());
        if (fragmentTabHost != null && bottomBarLayout != null) {
            fragmentTabHost.setCurrentTab(selectTab);
            bottomBarLayout.setSelect(selectTab);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activityStackClear(getName());
        AppConst.setIsMainActivityActive(true);
    }

    @Override
    protected boolean isCustomPv() {
        // 主tab 依赖的 Activity，不单独打PV。
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        AppConst.setIsMainActivityActive(false);
        LoginUtils.getInstance().resetCheckListener();
        EventBusUtils.unRegisterSticky(this);
    }

    @Override
    public void onBackPressed() {
        //开打图书的动画是否关闭了。没有就关闭
        BookView openedBookView = BookView.getOpenedBookView();
        if (null != openedBookView) {
            openedBookView.startCloseBookAnimation(null, EventConstant.REQUESTCODE_CLOSEDBOOK, EventConstant.TYPE_MAINSHELFFRAGMENT);
            BookView.setOpenedBookView(null);
        }
        if (isShelfCurrentManaging()) {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null) {
                ((MainShelfFragment) currentFragment).hideManagerMode(false);
            }
        } else {
            tryExitApp();
        }
    }

    /**
     * 准备退出应用
     */
    public void tryExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastAlone.showShort(getString(R.string.press_back_again));
            exitTime = System.currentTimeMillis();
        } else {
            ModelAction.exitApp(this, true);
        }
    }

    private Fragment getCurrentFragment() {
        String tag = fragmentTabHost.getCurrentTabTag();
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    public int getCurrentTab() {
        return fragmentTabHost.getCurrentTab();
    }

    @Override
    public void setBookStoreTableHost(final int select) {
        ALog.eZz("setBookStoreTableHost:selectTab:" + select);
        fragmentTabHost.setCurrentTab(select);
        bottomBarLayout.setSelect(select);
    }

    /**
     * 设置底部view显示状态
     *
     * @param isShowBottomView isShowBottomView
     */
    public void setBottomViewStatus(boolean isShowBottomView) {
        if (isShowBottomView) {
            if (mLinearLayout != null) {
                mLinearLayout.setVisibility(View.GONE);
            }
            if (mShelfManagerBottomView != null) {
                mShelfManagerBottomView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mShelfManagerBottomView != null) {
                mShelfManagerBottomView.setVisibility(View.GONE);
            }
            if (mLinearLayout != null) {
                mLinearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 是否书架处于正在编辑状态
     *
     * @return 状态
     */
    public boolean isShelfCurrentManaging() {
        Fragment currentFragment = getCurrentFragment();
        return currentFragment != null && currentFragment instanceof MainShelfFragment && ((MainShelfFragment) currentFragment).isOpenManager();
    }

    /**
     * 是否个人中心页面
     *
     * @return boolean
     */
    public boolean isPersonalCenter() {
        return getCurrentTab() == MainTabInfoUtils.TAB_PERSONAL_INDEX;
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        String type = event.getType();
        int requestCode = event.getRequestCode();
        Bundle mBundle = event.getBundle();
        switch (requestCode) {
            case EventConstant.UPDATA_FEATURED_URL_REQUESTCODE:
                if (EventConstant.TYPE_BOOK_STORE.equals(type)) {
                    mPresenter.onEventSetBookStore(mBundle);
                }
                break;
            case EventConstant.CODE_PUSH:
                if (EventConstant.TYPE_PUSH.equals(type)) {
                    mPresenter.onEventPush(mBundle);
                }
                break;
            case EventConstant.START_OPEN_BOOK:
                if (TextUtils.equals(type, EventConstant.TYPE_MAIN2ACTIVITY)) {
                    mPresenter.operSkipOtherPage(mBundle);
                }
                break;
            case EventConstant.CODE_SHOW_TMS_DIALOG:
                if (TextUtils.equals(type, EventConstant.TYPE_MAIN2ACTIVITY)) {
                    String thisUid = mBundle.getString("thisUid");
                    String accessToken = mBundle.getString("accessToken");
                    if (!TextUtils.isEmpty(thisUid) && !TextUtils.isEmpty(accessToken)) {
                        mPresenter.showTmsDialog(thisUid, accessToken);
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected boolean isAutoSysAppToken() {
        return true;
    }

    @Override
    public int getNavigationBarColor() {
        return R.color.color_100_f2f2f2;
    }

    @Override
    public int getStatusColor() {
        return R.color.color_100_ffffff;
    }

    @Override
    public void checkPermission() {
        String[] pnList = PermissionUtils.loadingPnList();
        boolean isGrant = checkPermission.checkPermissions(pnList);
        if (!isGrant) {
            checkPermission.requestPermissions(this, PermissionUtils.CODE_LOGO_REQUEST, pnList, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted() {
    }

    @Override
    public void onPermissionDenied() {
        checkPermission.showTipsDialog(this);
    }

    @Override
    protected boolean isNeedRegisterEventBus() {
        return false;
    }
}
