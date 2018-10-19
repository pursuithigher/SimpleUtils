package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.dzbook.BaseLoadActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.PersonCloudShelfUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import org.json.JSONException;
import org.json.JSONObject;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.bookDetail.BeanBookDetail;
import hw.sdk.net.bean.cloudshelf.BeanCloudShelfPageListInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * PersonCloudShelfPresenter
 *
 * @author dongdianzhou on 2017/11/20.
 */

public class PersonCloudShelfPresenter extends BasePresenter {

    private static final int TOTAL_NUM = 10;
    private LoginCheckUtils loginCheckUtils = null;
    private PersonCloudShelfUI mUI;
    private int index = 1;

    /**
     * 构造
     *
     * @param personCloudShelfUI personCloudShelfUI
     */
    public PersonCloudShelfPresenter(PersonCloudShelfUI personCloudShelfUI) {
        mUI = personCloudShelfUI;
    }

    /**
     * 填充云书架数据
     */
    public void getCloudShelfData() {
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.showNoNetView();
        } else {
            mUI.showLoadding();
            getCloudShelfDataFromNet(true, false);
        }
    }

    private boolean isActivityEmpty() {
        Activity activity = mUI.getActivity();
        return activity == null;
    }

    /**
     * 网络获取云书架数据
     *
     * @param isReference         isReference
     * @param isTokenInvalidRetry isTokenInvalidRetry
     */
    public void getCloudShelfDataFromNet(final boolean isReference, final boolean isTokenInvalidRetry) {
        Observable.create(new ObservableOnSubscribe<BeanCloudShelfPageListInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanCloudShelfPageListInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        BeanCloudShelfPageListInfo beanCloudShelfPageListInfo = HwRequestLib.getInstance().getCloudShelfPageList(index + "", TOTAL_NUM + "", mUI.getLastItemTime());
                        e.onNext(beanCloudShelfPageListInfo);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanCloudShelfPageListInfo>() {
            @Override
            public void onSubscribe(Disposable d) {
                if (!d.isDisposed()) {
                    composite.addAndDisposeOldByKey("getCloudShelfDataFromNet", d);
                }
            }

            @Override
            public void onNext(BeanCloudShelfPageListInfo value) {
                mUI.hideLoadding();
                mUI.compeletePullLoadMore();
                if (value.isSuccess()) {
                    if (value.isContainData()) {
                        mUI.setShelfData(value.list, isReference);
                        //是否有更多数据，1:有，0:没有
                        if (value.hasMore == 1) {
                            mUI.setLoadMore(true);
                        }
                    } else {
                        if (isReference || mUI.getCount() == 0) {
                            mUI.showEmptyView();
                        } else {
                            mUI.setLoadMore(false);
                            //                                    mUI.showMessage(R.string.no_more_data);
                            mUI.showAllTips();

                        }
                    }
                } else {

                    if (!isTokenInvalidRetry && value.isTokenExpireOrNeedLogin()) {
                        tokenInvalidRetry(isReference);
                        return;
                    }

                    if (isReference) {
                        mUI.showNoNetView();
                    } else {
                        mUI.showMessage(R.string.request_data_failed);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.hideLoadding();
                if (isReference) {
                    mUI.showNoNetView();
                } else {
                    mUI.compeletePullLoadMore();
                    mUI.showNoNetView();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
        if (loginCheckUtils != null) {
            loginCheckUtils.resetAgainObtainListener();
            loginCheckUtils.disHuaWeiConnect();
        }
    }

    /**
     * 继续阅读
     *
     * @param bookInfo bookInfo
     */
    public void continueReadBook(BookInfo bookInfo) {
        Activity mActivity = (Activity) mUI.getContext();
        ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.PERSON_CENTER_CLOUDSELF_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_CLOUDSELF_CONTINUEREAD_VALUE, 1);
        readBook(bookInfo, mActivity);
    }


    /**
     * 读书
     *
     * @param bookInfo  bookInfo
     * @param mActivity mActivity
     */
    private void readBook(BookInfo bookInfo, Activity mActivity) {
        final CatalogInfo catalog = DBUtils.getCatalog(mActivity, bookInfo.bookid, bookInfo.currentCatalogId);
        if (null != catalog) {
            if (catalog.isAvailable()) {
                ReaderUtils.intoReader(mActivity, catalog, catalog.currentPos);
            } else {
                if ("0".equals(catalog.isdownload)) {
                    CatalogInfo cinfo = new CatalogInfo(bookInfo.bookid, catalog.catalogid);
                    cinfo.isdownload = "1";
                    DBUtils.updateCatalog(mActivity, cinfo);
                }

                loadSingle(bookInfo, catalog);
            }
        } else {
            ToastAlone.showShort(mActivity.getResources().getString(R.string.preload_loading_fail));
        }
    }

    private void loadSingle(final BookInfo bookInfo, final CatalogInfo catalogInfo) {
        final BaseLoadActivity mActivity = (BaseLoadActivity) mUI.getContext();
        CatalogInfo firstCatalog = DBUtils.getFirstCatalog(mActivity, bookInfo.bookid);
        if (TextUtils.equals(catalogInfo.catalogid, firstCatalog.catalogid)) {
            //是第1个章节
            Disposable disposable = Observable.create(new ObservableOnSubscribe<LoadResult>() {

                @Override
                public void subscribe(ObservableEmitter<LoadResult> e) {

                    LoadResult loadResult = BookLoader.getInstance().fastReadChapter(mActivity, bookInfo.bookid, false);
                    e.onNext(loadResult);
                    e.onComplete();

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {
                @Override
                public void onNext(LoadResult value) {
                    if (null != mActivity) {
                        mActivity.dissMissDialog();
                    }
                    if (value == null) {
                        ALog.dZz("LoadResult null");
                        if (!NetworkUtils.getInstance().checkNet()) {
                            mUI.showNoNetView();
                        }
                        return;
                    }
                    if (value.isSuccess()) {
                        CatalogInfo info = DBUtils.getCatalog(mActivity, value.mChapter.bookid, value.mChapter.catalogid);

                        ReaderUtils.intoReader(mActivity, info, info.currentPos);

                    } else {
                        ALog.dZz("LoadResult:" + value.status);
                        //                        if (!value.isCanceled()) {
                        //                            // FIXME: cmt 2018/4/24 章节下载错误 待取数sdk完善
                        //                        }
                        if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                            if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mActivity != null) {
                                mActivity.showNotNetDialog();
                            }
                        } else {
                            ReaderUtils.dialogOrToast(mActivity, value.getMessage(mActivity), true, bookInfo.bookid);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mActivity.dissMissDialog();
                }

                @Override
                public void onComplete() {

                }

                @Override
                protected void onStart() {
                    mActivity.showDialogByType(DialogConstants.TYPE_GET_DATA);
                }
            });

            composite.addAndDisposeOldByKey("loadSingle", disposable);
        } else {
            RechargeParams rechargeParams = new RechargeParams("3", bookInfo);
            rechargeParams.setOperateFrom(mActivity.getName());
            rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_10);
            mActivity.loadChapter(mActivity, catalogInfo, bookInfo, rechargeParams);
        }

    }

    /**
     * 添加书架
     *
     * @param bookId bookId
     */
    public void addBookShelf(final String bookId) {
        final BaseLoadActivity mActivity = (BaseLoadActivity) mUI.getContext();
        ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.PERSON_CENTER_CLOUDSELF_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_CLOUDSELF_ADDSELF_VALUE, 1);
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.showNoNetView();
            return;
        }
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                BeanBookDetail bookDetailBean = null;
                BeanBookInfo beanBookInfo = null;
                try {
                    bookDetailBean = HwRequestLib.getInstance().bookdetailRequest(bookId);
                    beanBookInfo = bookDetailBean.book;
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                }
                if (null != bookDetailBean && null != beanBookInfo) {

                    if (ListUtils.isEmpty(bookDetailBean.chapters)) {
                        e.onError(new Exception(""));
                        return;
                    }

                    if (beanBookInfo.isDeleteOrUndercarriage()) {
                        ToastAlone.showShort(mActivity.getString(R.string.book_down_shelf));
                        return;
                    }

                    InsertBookInfoDataUtil.appendBookAndChapters(mActivity, bookDetailBean.chapters, beanBookInfo, true, null);

                    BookInfo mBookInfo = new BookInfo();
                    mBookInfo.bookid = bookId;
                    mBookInfo.hasRead = 2;
                    mBookInfo.isAddBook = 2;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = WhiteListWorker.setPnPi(mActivity, jsonObject);
                    handleBookReaderFrom(mBookInfo, jsonObject);
                    DBUtils.updateBook(mActivity, mBookInfo);
                    e.onNext("");
                } else {
                    e.onError(new Exception(""));
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                composite.addAndDisposeOldByKey("addBookShelf", d);
            }

            @Override
            public void onNext(String value) {
                mUI.referenceAdapter();
            }

            @Override
            public void onError(Throwable e) {
                ToastAlone.showShort(mActivity.getResources().getString(R.string.add_book_shelf_fail_retry));
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void handleBookReaderFrom(BookInfo mBookInfo, JSONObject jsonObject) {
        try {
            jsonObject.put(LogConstants.GH_TYPE, WhiteListWorker.CLOUDBOOKSHELF_ACTIVITY_VALUE);
            String readerFrom = jsonObject.toString();
            if (!TextUtils.isEmpty(readerFrom)) {
                mBookInfo.readerFrom = readerFrom;
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 重置index
     *
     * @param isAdd isAdd
     */
    public void resetIndex(boolean isAdd) {
        if (isAdd) {
            index += 1;
            return;
        }
        index = 1;
    }

    /**
     * 长按删除
     *
     * @param beanBookInfo beanBookInfo
     */
    public void deleteItems(BeanBookInfo beanBookInfo) {
        mUI.popDeleteDialog(beanBookInfo);
    }

    /**
     * 只重试一次
     */
    private void tokenInvalidRetry(final boolean isReference) {
        mUI.showLoadding();
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken((Activity) mUI.getContext(), new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {
                mUI.dissMissDialog();

                if (isReference) {
                    mUI.showNoNetView();
                } else {
                    mUI.showMessage(R.string.request_data_failed);
                }
            }

            @Override
            public void loginComplete() {
                getCloudShelfDataFromNet(true, true);
            }
        });
    }

    /**
     * 删除书籍同步
     *
     * @param beanBookInfo beanBookInfo
     */
    public void deleteItemsSyncNet(final BeanBookInfo beanBookInfo) {
        Observable.create(new ObservableOnSubscribe<BeanCloudShelfPageListInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanCloudShelfPageListInfo> e) {
                try {
                    if (!isActivityEmpty()) {
                        BeanCloudShelfPageListInfo beanCloudShelfPageListInfo = HwRequestLib.getInstance().deleteCloudShelfData(beanBookInfo.bookId, TOTAL_NUM + "");
                        e.onNext(beanCloudShelfPageListInfo);
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanCloudShelfPageListInfo>() {
            @Override
            public void onSubscribe(Disposable d) {
                composite.addAndDisposeOldByKey("deleteItemsSyncNet", d);
            }

            @Override
            public void onNext(BeanCloudShelfPageListInfo value) {
                if (value.isSuccess()) {
                    mUI.deleteDataFromAdapter(beanBookInfo);
                    //                            if (value.isContainData()) {
                    //                                mUI.setShelfData(value.list, true);
                    //                                //是否有更多数据，1:有，0:没有
                    //                                if (value.hasMore != 1) {
                    //                                    mUI.setLoadMore(false);
                    //                                } else {
                    //                                    mUI.setLoadMore(true);
                    //                                }
                    //                            } else {
                    //                                mUI.showEmptyView();
                    //                            }
                } else {
                    mUI.showMessage(R.string.str_cloudshelf_delete_failed);
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.initNetErrorStatus();
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public void setPage(int pPage) {
        index = pPage;
    }
}
