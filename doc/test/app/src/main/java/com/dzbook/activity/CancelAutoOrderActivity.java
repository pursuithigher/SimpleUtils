package com.dzbook.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.adapter.CancelAutoOrderAdapter;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.utils.DBUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 取消自动订购
 *
 * @author lizhongzhong 2015/9/21.
 */
public class CancelAutoOrderActivity extends BaseSwipeBackActivity {

    private static final String TAG = "CancelAutoOrderActivity";
    private ListView listViewAutoCancelOrder;

    private CancelAutoOrderAdapter cancelAutoOrderAdapter = null;

    private StatusView statusView;

    private DianZhongCommonTitle mCommonTitle;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_set_cancel_auto_order);
    }

    @Override
    protected void initView() {
        statusView = findViewById(R.id.statusView);
        mCommonTitle = findViewById(R.id.include_top_title_item);
        listViewAutoCancelOrder = findViewById(R.id.listview_auto_cancel_order);
    }

    @Override
    protected void initData() {

        cancelAutoOrderAdapter = new CancelAutoOrderAdapter(this);
        listViewAutoCancelOrder.setAdapter(cancelAutoOrderAdapter);

        getAllBookPayRemind();

    }

    @NonNull
    private void getAllBookPayRemind() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<ArrayList<BookInfo>>() {

            @Override
            public void subscribe(ObservableEmitter<ArrayList<BookInfo>> e) throws Exception {

                ArrayList<BookInfo> bookInfos = DBUtils.findAllNetBooksByPayRemind(CancelAutoOrderActivity.this);
                e.onNext(bookInfos);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<ArrayList<BookInfo>>() {

            @Override
            public void onNext(ArrayList<BookInfo> result) {

                if (result != null && result.size() > 0) {
                    cancelAutoOrderAdapter.addItem(result, true);
                } else {
                    statusView.showEmpty(getResources().getString(R.string.hua_wei_no_auto_purchase_section_is_set));
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        compositeDisposable.addAndDisposeOldByKey("getAllBookPayRemind", disposable);

    }

    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.disposeAll();
    }
}
