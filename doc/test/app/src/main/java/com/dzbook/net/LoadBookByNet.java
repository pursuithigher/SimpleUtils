package com.dzbook.net;

import android.app.Activity;

import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.pay.LoadBookListener;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.utils.WhiteListWorker;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.BeanSingleBookInfo;
import hw.sdk.net.bean.store.BeanGetBookInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * LoadBookByNet
 *
 * @author caimantang on 2018/1/15.
 */

public class LoadBookByNet {

    /**
     * 领书标记
     */
    public static final String GET_BOOK_FROM_NET_STORE = "1";

    private static volatile LoadBookByNet loadBookByNet;

    /**
     * 获取LoadBookByNet实例
     *
     * @return 实例
     */
    public static LoadBookByNet getInstance() {
        if (loadBookByNet == null) {
            synchronized (LoadBookByNet.class) {
                if (loadBookByNet == null) {
                    loadBookByNet = new LoadBookByNet();
                }
            }
        }
        return loadBookByNet;
    }


    /**
     * 164接口 添加限免限价书籍
     *
     * @param activity         ：上下文
     * @param productId        ：商品id
     * @param bookId           ：书籍id
     * @param type             ：领取类型
     * @param loadBookListener loadBookListener
     */
    public void addBookToShelf(final Activity activity, final String productId, final String bookId, final String type, final LoadBookListener loadBookListener) {
        Observable.create(new ObservableOnSubscribe<BeanGetBookInfo>() {
            @Override
            public void subscribe(ObservableEmitter<BeanGetBookInfo> e) {
                try {
                    BeanGetBookInfo beanGetBookInfo = HwRequestLib.getInstance().getBookInfoFromNet(productId, bookId, type);
                    e.onNext(beanGetBookInfo);
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BeanGetBookInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BeanGetBookInfo value) {
                if (value.isSuccess()) {
                    if (value.isGetSuccess()) {
                        if (value.isContainItems()) {
                            BeanSingleBookInfo beanSigleBookInfo = value.books.get(0);
                            if (beanSigleBookInfo != null) {
                                InsertBookInfoDataUtil.insertNativeBook(activity, value, WhiteListWorker.STORE_FREE_GETBOOK);
                                loadBookListener.success(value.status, value.message, beanSigleBookInfo.bookInfo);
                                return;
                            }
                        }
                        loadBookListener.success(value.status, value.message, null);
                    } else {
                        loadBookListener.fail(value.status, value.message);
                    }
                } else {
                    ToastAlone.showShort(value.getRetMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                if (activity instanceof BaseActivity) {
                    ((BaseActivity) activity).showNotNetDialog();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
