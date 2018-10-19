package com.dzbook.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.dzbook.AppConst;
import com.dzbook.activity.Main2Activity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.adapter.shelf.DzShelfDelegateAdapter;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.UserGrow;
import com.dzbook.mvp.UI.MainShelfUI;
import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.service.InitBookRunnable;
import com.dzbook.utils.ImmersiveUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.hw.PermissionUtils;
import com.dzbook.view.DzComTitleIndex;
import com.dzbook.view.common.dialog.CustomCheckMoreDialog;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.dzbook.view.common.loading.RefreshLayout;
import com.dzbook.view.shelf.ShelfManagerTitleView;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hw.sdk.net.bean.cloudshelf.BeanCloudShelfLoginSyncInfo;
import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;

/**
 * 书架
 *
 * @author dongdianzhou 2018/04/12
 */

public class MainShelfFragment extends BaseFragment implements MainShelfUI {
    /**
     * tag
     */
    public static final String TAG = "MainShelfFragment";
    private static final int REFERENCE_DIS = 30000;

    private DzComTitleIndex mShelfTitleView;
    private RefreshLayout mShelfRefresh;
    private ShelfManagerTitleView mShelfManagerTitleView;
    private RecyclerView mShelfRecyclerView;

    private DzShelfDelegateAdapter mAdapter;

    private boolean isSyncBooksLog = false;

    private MainShelfPresenter mPresenter;

    private boolean isClosedAnim = false;

    private CustomCheckMoreDialog shelfSortDialog;
    private CustomHintDialog dialog;

    @Override
    public String getTagName() {
        return "MainShelfFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_shelf2, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateShelfSignIn(null);
        if (!needCloseAnimBook()) {
            isClosedAnim = false;
            if (!isOpenManager()) {
                mPresenter.getBookFromLocal(true);
            }
        }
        //打点
        HashMap<String, String> map = new HashMap<>();
        map.put(LogConstants.KEY_BOOK_SHELF_SJMS, SpUtil.getinstance(getActivity()).getBookShelfMode() + "");
        DzLog.getInstance().logPv(this, map, null);

        UserGrow.synchrodataServerReaderTime(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegisterSticky(this);
        if (mShelfTitleView != null) {
            mShelfTitleView.destroyPopWindow();
        }
        if (shelfSortDialog != null && shelfSortDialog.isShow()) {
            shelfSortDialog.dismiss();
            shelfSortDialog = null;
        }
        if (dialog != null && dialog.isShow()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void initView(View uiView) {
        mShelfTitleView = uiView.findViewById(R.id.shelftitleview);
        mShelfTitleView.setShelfUI(this);
        mShelfTitleView.setSource(true);
        mShelfManagerTitleView = uiView.findViewById(R.id.shelfmanagertitleview);
        mShelfRefresh = uiView.findViewById(R.id.srl_shelf_refresh);
        mShelfRecyclerView = uiView.findViewById(R.id.rv_bookshelf);
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (isOpenManager()) {
//            hideManagerMode(false);
//        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        if (isInMultiWindowMode) {
            if (mShelfTitleView != null) {
                mShelfTitleView.destroyPopWindow();
            }
            if (shelfSortDialog != null && shelfSortDialog.isShow()) {
                shelfSortDialog.dismiss();
                shelfSortDialog = null;
            }
            if (dialog != null && dialog.isShow()) {
                dialog.dismiss();
                dialog = null;
            }
        }
        super.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    protected void initData(View uiView) {
        mPresenter = new MainShelfPresenter(getActivity(), this);
        if (getActivity() != null) {
            ((Main2Activity) getActivity()).getShelfManagerBottomView().setMainShelfUI(this);
        }
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(getContext());
        mShelfRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DzShelfDelegateAdapter(layoutManager, true, getContext(), this, mPresenter);
        mShelfRecyclerView.setAdapter(mAdapter);
        //设置书架模式
        int mode = SpUtil.getinstance(getActivity()).getBookShelfMode();
        mAdapter.shelfShowMode = mode;
        //日志打印 和友盟日志打印
        mPresenter.dzLogAndUmengLog();
        //初始化粘性事件
        EventBusUtils.registerSticky(this);
        //校验是否目录是否是目录 而不是文件
        mPresenter.checkFileAndOperFileRoot();
        //初始化书籍由于异常情况下 更新标记为正在更新 还原为未更新状态
        mPresenter.initBookUpdatingToNoUpdate();
    }

    @Override
    protected void setListener(View uiView) {
        if (mShelfRefresh != null) {
            mShelfRefresh.setRefreshListener(new RefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    reference();
                }
            });
        }
        mShelfManagerTitleView.setClosedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideManagerMode(false);
            }
        });
    }

    /**
     * 刷新跑马灯和书架更新消息
     */
    private void reference() {
//        String time = SpUtil.getinstance(getActivity()).getString("time");
//        if ("".equals(time)) {
//            SpUtil.getinstance(getActivity()).setString("time", System.currentTimeMillis() + "");
        mPresenter.getShelfUpdateAndNotify(null, true, false);
//        } else {
//            Long timeInterval = System.currentTimeMillis() - Long.parseLong(time);
//            if (timeInterval > REFERENCE_DIS) {
//                mPresenter.getShelfUpdateAndNotify(null, true, false);
//                SpUtil.getinstance(getActivity()).setString("time", System.currentTimeMillis() + "");
//            } else {
//                mShelfRefresh.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mShelfRefresh.isRefreshing()) {
//                            mShelfRefresh.setRefreshing(false);
//                        }
//                    }
//                }, 2000);
//            }
//        }
//
//        mShelfRefresh.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mShelfRefresh.isRefreshing()) {
//                    mShelfRefresh.setRefreshing(false);
//                }
//            }
//        }, 4000);
    }

    /**
     * 打开书架编辑
     *
     * @param bookId bookId
     */
    public void openManagerMode(String bookId) {
        Activity activity = getActivity();
        if (activity != null) {
            ImmersiveUtils.init(activity, R.color.color_100_f2f2f2, R.color.color_100_f2f2f2);
        }
        if (mShelfTitleView != null) {
            mShelfTitleView.setVisibility(View.INVISIBLE);
        }
        mShelfRefresh.setShelfEnabled(false);
        mShelfManagerTitleView.setVisibility(View.VISIBLE);
        boolean isNull = TextUtils.isEmpty(bookId);
        if (isNull) {
            mShelfManagerTitleView.setTitleText(0);
        } else {
            mShelfManagerTitleView.setTitleText(1);
        }
        if (getActivity() != null) {
            ((Main2Activity) getActivity()).setBottomViewStatus(true);
            ((Main2Activity) getActivity()).getShelfManagerBottomView().setAllSelectViewStatus(true);
            ((Main2Activity) getActivity()).getShelfManagerBottomView().setDeleteManageEnable(!isNull);
        }
        mAdapter.setCurrentManagerMode(DzShelfDelegateAdapter.MODE_MANAGER, bookId, false);
    }

    /**
     * 关闭编辑状态
     *
     * @param isReferenceShelfData isReferenceShelfData
     */
    public void hideManagerMode(boolean isReferenceShelfData) {
        Activity activity = getActivity();
        if (activity != null) {
            ImmersiveUtils.init(activity, R.color.color_100_fcfcfc, R.color.color_100_f2f2f2);
        }
        if (mShelfTitleView != null) {
            mShelfTitleView.setVisibility(View.VISIBLE);
        }
        mShelfRefresh.setShelfEnabled(true);
        mShelfManagerTitleView.setVisibility(View.GONE);
        if (getActivity() != null) {
            ((Main2Activity) getActivity()).setBottomViewStatus(false);
        }
        mAdapter.setCurrentManagerMode(DzShelfDelegateAdapter.MODE_COMMON, "", isReferenceShelfData);
    }

    @Override
    public Context getContext() {
        Context context = getActivity();
        if (null == context) {
            return AppConst.getApp();
        }
        return context;
    }

    @Override
    public void setRecycleViewSelection() {
        mShelfRecyclerView.scrollToPosition(0);
    }

    @Override
    public void openManager(String bookid) {
        boolean isInitBooksCompelete = SpUtil.getinstance(getContext()).getBoolean(InitBookRunnable.IS_BOOK_INIT, true);
        if (isInitBooksCompelete) {
            openManagerMode(bookid);
        } else {
            ToastAlone.showShort(R.string.toast_shlef_try_later);
        }
    }

    @Override
    public void setBookShlefMode(int mode) {
        mAdapter.setCurrentShelfMode(mode);
        SpUtil.getinstance(getContext()).setBookShelfMode(mode);
    }

    @Override
    public void setBookShlefData(final List<BookInfo> books) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //同步一次书架信息 每次启动至同步一次
                if (!isSyncBooksLog) {
                    if (!ListUtils.isEmpty(books)) {
                        isSyncBooksLog = true;
                        ArrayList<String> list = new ArrayList<>();
                        int size = books.size();
                        if (books.size() >= 20) {
                            size = 20;
                        }
                        for (int i = 0; i < size; i++) {
                            list.add(books.get(i).bookid);
                        }
                        HashMap<String, String> map = new HashMap<>();
                        map.put("bids", list.toString());
                        DzLog.getInstance().logEvent(LogConstants.EVENT_SJSJ, map, null);
                    }
                }

                mAdapter.addItems(books);
                if (!isClosedAnim) {
                    cloudBookShelfSynchronize();
                }
            }
        });
    }


    @Override
    public void hideReferenceDelay() {
        mShelfRefresh.post(new Runnable() {
            @Override
            public void run() {
                if (mShelfRefresh.isRefreshing()) {
                    mShelfRefresh.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public List<BookInfo> getShelfAdapterDatas() {
        return mAdapter.getShelfDatas();
    }

    @Override
    public void setAllItemSelectStatus(boolean isAllSelected) {
        mAdapter.setAllItemSelectStatus(isAllSelected);
        List<BookInfo> list = mAdapter.getAllSelectedBooks();
        if (list != null) {
            mShelfManagerTitleView.setTitleText(list.size());
        }
    }


    @Override
    public void popDeleteBookDialog() {
        final List<BookInfo> list = mAdapter.getAllSelectedBooks();
        if (list != null && list.size() > 0) {
            if (dialog == null) {
                dialog = new CustomHintDialog(getContext());
            }
            int size = list.size();
            if (size == 1) {
                dialog.setDesc(getString(R.string.str_shelf_delete_this_books));
            } else {
                dialog.setDesc(String.format(getString(R.string.str_shelf_delete_books), size));
            }

            dialog.setConfirmTxt(getString(R.string.delete));
            dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
                @Override
                public void clickConfirm(Object object) {
                    mPresenter.deleteAllSelectBooks(list);
                }

                @Override
                public void clickCancel() {
                }
            });
            dialog.show();
        } else {
            ToastAlone.showShort(R.string.toast_delete_size_not_empty);
        }
    }

    @Override
    public void needShowSetNotifyDialogIfNeed(int checkNotifyAppOpenCount, int checkNotifyFrequency, String cnMsg) {
        PermissionUtils permissionUtils = new PermissionUtils();
        permissionUtils.showNotifySettingIfNeed(getActivity(), checkNotifyAppOpenCount, checkNotifyFrequency, cnMsg);
    }

    @Override
    public void backToCommonMode(boolean isReferenceShelfData) {
        if (!isReferenceShelfData) {
            //排序返回
            String sortType = SpUtil.getinstance(getContext()).getString(SpUtil.SHELF_BOOK_SORT, "0");
            mAdapter.sortShelfData(sortType);
        }
        hideManagerMode(isReferenceShelfData);
    }


    @Override
    public void syncCloudBookShelfSuccess(BeanCloudShelfLoginSyncInfo beanInfo) {
        //刷新书架书籍信息
        SpUtil spUtil = SpUtil.getinstance(getActivity());
        spUtil.setBoolean(SpUtil.IS_ALREADY_SHOW_CLOUD_DIALOG + spUtil.getUserID(), true);
        mPresenter.getBookFromLocal(false);
        if (beanInfo.hasMore()) {
            ToastAlone.showShort(getString(R.string.str_synccloudshelf_more));
        } else {
            ToastAlone.showShort(getString(R.string.str_synccloudshelf));
        }
    }

    @Override
    public void updateShelfData(final List<BookInfo> books, BeanBookUpdateInfo updateInfo) {
        if (mAdapter != null) {
            mAdapter.setUpdateInfo(updateInfo);
            mAdapter.addItems(books);
        }
    }

    @Override
    public void updateShelfSignIn(BeanBookUpdateInfo value) {
        mAdapter.referenceSignInStatus(value);
    }

    @Override
    public void popSortDialog() {
        if (shelfSortDialog == null) {
            int checkIndex = -1;
            String booksSort = SpUtil.getinstance(getContext()).getString(SpUtil.SHELF_BOOK_SORT, "0");
            //时间
            if (TextUtils.equals(booksSort, "0")) {
                checkIndex = 1;
            } else if (TextUtils.equals(booksSort, "1")) {
                checkIndex = 0;
            }
            List<String> list = new ArrayList<>();
            list.add(getResources().getString(R.string.str_shelf_sorttype_name));
            list.add(getResources().getString(R.string.str_shelf_sorttype_time));
            shelfSortDialog = new CustomCheckMoreDialog(getContext(), CustomDialogBusiness.STYLE_DIALOG_CANCEL);
            shelfSortDialog.setTitle(getResources().getString(R.string.str_shelf_sort_type));
            shelfSortDialog.setData(list, checkIndex, true);
            shelfSortDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface curDialog) {
                    shelfSortDialog = null;
                }
            });
            shelfSortDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
                @Override
                public void clickConfirm(Object object) {
                    int index = (int) object;
                    switch (index) {
                        case 0:
                            SpUtil.getinstance(getContext()).setString(SpUtil.SHELF_BOOK_SORT, "1");
                            backToCommonMode(true);
                            break;
                        case 1:
                            SpUtil.getinstance(getContext()).setString(SpUtil.SHELF_BOOK_SORT, "0");
                            backToCommonMode(true);
                            break;
                        default:
                            shelfSortDialog.dismiss();
                            break;
                    }
                }

                @Override
                public void clickCancel() {
                    shelfSortDialog.dismiss();
                }
            });
        }
        shelfSortDialog.show();
    }

    /**
     * 云书架书籍同步弹窗
     */
    public void cloudBookShelfSynchronize() {
        SpUtil spUtil = SpUtil.getinstance(getActivity());
        final String json = spUtil.getString(SpUtil.SYNCH_CLOUD_BOOKS_JSON + spUtil.getUserID());
        if (!TextUtils.isEmpty(json) && !spUtil.getBoolean(SpUtil.IS_ALREADY_SHOW_CLOUD_DIALOG + spUtil.getUserID())) {
            ThirdPartyLog.onEvent(getActivity(), ThirdPartyLog.SHELF_SYSN_CLOUD_START_SU);
            mPresenter.syncCloudBookShelf(json);
        }
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

    public boolean isOpenManager() {
        return mAdapter != null && mAdapter.getCurrentManagerMode() == DzShelfDelegateAdapter.MODE_MANAGER;
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        int requestCode = event.getRequestCode();
        String type = event.getType();
        if (EventConstant.TYPE_MAINSHELFFRAGMENT.equals(type)) {
            switch (requestCode) {
                case EventConstant.REQUESTCODE_REFERENCESHELFMANAGERVIEW:
                    List<BookInfo> list = mAdapter.getAllSelectedBooks();
                    if (list != null) {
                        int size = list.size();
                        mShelfManagerTitleView.setTitleText(list.size());
                        if (getActivity() != null) {
                            ((Main2Activity) getActivity()).getShelfManagerBottomView().setAllSelectViewStatus(mAdapter.isAllSelect());
                            ((Main2Activity) getActivity()).getShelfManagerBottomView().setDeleteManageEnable(size > 0);
                        }
                    }
                    break;
                case EventConstant.REQUESTCODE_SIGNINSUCCESS:
                    //签到h5页面，签到成功回调，手动修改签到状态
                    SpUtil.getinstance(getContext()).markTodayByKey(SpUtil.SP_USER_SIGN);
                    BeanBookUpdateInfo updateInfo = new BeanBookUpdateInfo();
                    updateInfo.hasSignIn = 1;
                    mAdapter.setUpdateInfo(updateInfo);
                    break;
                case EventConstant.LOGIN_SUCCESS_UPDATE_SHELF:
                    mPresenter.getShelfUpdateAndNotify(null, true, false);
                    break;
                case EventConstant.LOGIN_SUCCESS_UPDATE_CLOUDSHELF_SYNC:
                    cloudBookShelfSynchronize();
                    break;
                case EventConstant.REQUESTCODE_CLOSEDBOOK:
                    mPresenter.getBookFromLocal(true);
                    cloudBookShelfSynchronize();

                    break;
                case EventConstant.REQUESTCODE_OPENBOOK:
                    //打开书的动画
                    mActivity.dissMissDialog();

                    CatalogInfo catalog = (CatalogInfo) event.getBundle().getSerializable(EventConstant.CATALOG_INFO);
                    if (catalog != null) {
                        ReaderUtils.intoReader(getActivity(), catalog, catalog.currentPos);
                    }

                    mPresenter.getBookFromLocal(false);
                    //刷新listview
                    mPresenter.refreshBookShelfSelection();

                    break;
                case EventConstant.SHELF_LOCAL_REFRESH:
                    ALog.dLk("内置书以后更新本地书架");
                    referenceDataAfterInitBooks();
                    break;
                case EventConstant.CLOSEBOOK_REQUEST_CODE:
                    closeShelfBook();
                    break;
                default:
                    break;
            }
        }
    }

    private void closeShelfBook() {
        if (needCloseAnimBook()) {
            isClosedAnim = true;
            if (isVisible()) {
                mPresenter.closedBookAnim(mShelfRecyclerView);
            } else {
                mPresenter.closedBookDirect();
            }
        }
    }

    private boolean needCloseAnimBook() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !TextUtils.isEmpty(mPresenter.getBookViewId());
    }

    /**
     * 内置书以后更新本地书架
     */
    public void referenceDataAfterInitBooks() {
        if (mPresenter != null) {
            mPresenter.getBookFromLocal(false);
        }
    }
}
