package com.dzbook;

import android.app.Activity;
import android.text.TextUtils;

import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.NetworkUtils;
import com.iss.app.BaseActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * base activity
 *
 * @author zhenglk
 */
public abstract class BaseLoadActivity extends BaseActivity {
    @Override
    public BaseActivity getActivity() {
        return this;
    }

    /**
     * 加载章节
     *
     * @param context        context
     * @param catalogInfo    catalogInfo
     * @param bookInfo       bookInfo
     * @param rechargeParams rechargeParams
     */
    public void loadChapter(final Activity context, final CatalogInfo catalogInfo, final BookInfo bookInfo, final RechargeParams rechargeParams) {
        Observable
                .create(new ObservableOnSubscribe<LoadResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<LoadResult> e) {

                        LoadResult result = BookLoader.getInstance().loadOneChapter(context, bookInfo, catalogInfo, rechargeParams);
                        if (result != null) {
                            result.mChapter = catalogInfo;
                        }
                        e.onNext(result);
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoadResult>() {

                    @Override
                    public void onNext(LoadResult value) {
                        dissMissDialog();

                        if (value == null) {
                            ALog.dZz("LoadResult null");
                            if (context instanceof BaseActivity) {
                                ((BaseActivity) context).showNotNetDialog();
                            }
                            return;
                        }
                        if (value.isSuccess()) {
                            CatalogInfo info = DBUtils.getCatalog(getContext(), value.mChapter.bookid, value.mChapter.catalogid);
                            ReaderUtils.intoReader(context, info, info.currentPos);
                        } else {
                            ALog.dZz("LoadResult:" + value.status);
                            if (value.isNetError() && !NetworkUtils.getInstance().checkNet()) {
                                if (!TextUtils.isEmpty(value.getMessage(context)) && context != null && context instanceof BaseActivity) {
                                    ((BaseActivity) context).showNotNetDialog();
                                }
                            } else {
                                ReaderUtils.dialogOrToast(context, value.getMessage(getContext()), true, bookInfo.bookid);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ALog.eZz("load ex:" + e.getMessage());
                        dissMissDialog();
                    }

                    @Override
                    public void onComplete() {
                        ALog.dZz("load onComplete");
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        showDialogLight();
                    }
                });
    }


}
