package com.dzbook.mvp.presenter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.dzbook.adapter.MainTypeLeftAdapter;
import com.dzbook.adapter.MainTypeRightAdapter;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.NativeTypeIndexUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.SpUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.type.BeanMainType;
import hw.sdk.net.bean.type.BeanMainTypeLeft;
import hw.sdk.net.bean.type.BeanMainTypeRight;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * MainTypePresenterImpl
 *
 * @author Winzows  2018/2/27
 */

public class MainTypePresenterImpl extends BasePresenter {

    private CompositeDisposable composite = new CompositeDisposable();

    private NativeTypeIndexUI mUI;
    private BeanMainType mainNativeTypeBean;
    private MainTypeRightAdapter detailAdapter;
    private MainTypeLeftAdapter listAdapter;

    /**
     * 构造
     *
     * @param mUI mUI
     */
    public MainTypePresenterImpl(NativeTypeIndexUI mUI) {
        this.mUI = mUI;
    }


    /**
     * 初始化RecycleView
     *
     * @param left  left
     * @param right right
     */
    public void initRecycleView(RecyclerView left, RecyclerView right) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mUI.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        left.setLayoutManager(linearLayoutManager);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mUI.getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        right.setLayoutManager(gridLayoutManager);
    }

    /**
     * 请求数据
     *
     * @param cacheJson 换群数据
     */
    public void requestData(final String cacheJson) {
        Observable<BeanMainType> observable = Observable.create(new ObservableOnSubscribe<BeanMainType>() {
            @Override
            public void subscribe(ObservableEmitter<BeanMainType> e) {
                try {
                    BeanMainType beanInfo;
                    if (!TextUtils.isEmpty(cacheJson)) {
                        beanInfo = new BeanMainType().parseJSON(new JSONObject(cacheJson));
                    } else {
                        beanInfo = HwRequestLib.getInstance().getMainTypeIndex();
                    }
                    e.onNext(beanInfo);
                    if (beanInfo.isSuccess()) {
                        saveHttpCache(beanInfo);
                    }
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }
        });
        Disposable disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanMainType>() {
            @Override
            public void onNext(final BeanMainType value) {
                if (value != null) {
                    if (value.isSuccess()) {
                        mainNativeTypeBean = value;
                        ArrayList<BeanMainTypeLeft> categoryNameList = value.getCategoryNameList();
                        if (categoryNameList != null && categoryNameList.size() > 0) {
                            mUI.bindLeftCatalogData(categoryNameList);
                            mUI.showView();
                        } else {
                            mUI.showEmpty();
                        }
                    } else {
                        mUI.onError();
                    }
                } else {
                    mUI.onError();
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                mUI.onError();
                ALog.printExceptionWz(e);
            }

            @Override
            public void onComplete() {
                mUI.dissMissDialog();
            }

            @Override
            protected void onStart() {
                super.onStart();
                mUI.onRequestData();
            }
        });
        composite.addAndDisposeOldByKey("requestTypeData", disposable);
    }

    /**
     * 保留上次的缓存
     *
     * @param value
     */
    private void saveHttpCache(final BeanMainType value) {
        if (value.jsonObj != null && !TextUtils.isEmpty(value.jsonObj.toString())) {
            HttpCacheInfo cacheInfo = new HttpCacheInfo();
            cacheInfo.url = RequestCall.MAIN_TYPE_INDEX;
            cacheInfo.gmt_create = System.currentTimeMillis() + "";
            cacheInfo.response = value.jsonObj.toString();
            DBUtils.updateOrInsertHttpCacheInfo(mUI.getContext(), cacheInfo);
        }
    }

    /**
     * 绑定数据
     *
     * @param recyclerView recyclerView
     * @param list         list
     */
    public void bindLeftCatalogData(RecyclerView recyclerView, ArrayList<BeanMainTypeLeft> list) {
        if (list != null) {
            if (listAdapter == null) {
                listAdapter = new MainTypeLeftAdapter(mUI.getContext());
            }
            listAdapter.putData(list);
            recyclerView.setAdapter(listAdapter);
            listAdapter.setOnItemClickListener(new MainTypeLeftAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(BeanMainTypeLeft categoryIndexBean, int leftPosition) {
                    mUI.onCatalogSelect(categoryIndexBean, leftPosition);
                }
            });
            setDefaultSelect(list);
        }
    }

    /**
     * 设置默认选择的item
     *
     * @param list
     */
    private void setDefaultSelect(ArrayList<BeanMainTypeLeft> list) {
        if (list != null && list.size() > 0) {
            /**
             * 2：女生
             * 1：男生
             * 0:跳过
             */

            int personReadPref = SpUtil.getinstance(mUI.getContext()).getPersonReadPref();
            int defaultSelect = 0;

            if (personReadPref == 1 || personReadPref == 2) {
                String defaultSex = personReadPref == 1 ? "男" : "女";
                for (int i = 0; i < list.size(); i++) {
                    BeanMainTypeLeft itemText = list.get(i);
                    if (itemText != null && !TextUtils.isEmpty(itemText.categoryName)) {
                        if (itemText.categoryName.contains(defaultSex)) {
                            defaultSelect = i;
                            break;
                        }
                    }
                }
            }
            listAdapter.setSelectItem(defaultSelect);
            mUI.onCatalogSelect(list.get(defaultSelect), defaultSelect);
        }
    }


    /**
     * 点击分类左侧栏后 刷新右边数据
     *
     * @param recyclerViewDetail recyclerViewDetail
     * @param list               list
     * @param categoryId         categoryId
     * @param leftPosition       leftPosition
     * @param categoryName       categoryName
     */
    public void bindRightCatalogData(RecyclerView recyclerViewDetail, ArrayList<BeanMainTypeRight> list, String categoryId, String categoryName, int leftPosition) {
        if (detailAdapter == null) {
            detailAdapter = new MainTypeRightAdapter(mUI.getContext());
            recyclerViewDetail.setAdapter(detailAdapter);
        }
        detailAdapter.putData(list, categoryId, categoryName, leftPosition);
    }

    /**
     * 点击分类左侧栏后 刷新右边数据
     *
     * @param categoryIndexBean categoryIndexBean
     * @param leftPosition      leftPosition
     */
    public void onLeftCatalogSelect(BeanMainTypeLeft categoryIndexBean, int leftPosition) {
        if (mainNativeTypeBean != null) {
            String categoryId = "";
            if (null != categoryIndexBean) {
                categoryId = categoryIndexBean.categoryId;
            }
            ArrayList<BeanMainTypeRight> categoryDetailByCategoryName = mainNativeTypeBean.getCategoryDetailByCategoryName(categoryIndexBean);
            if (categoryDetailByCategoryName != null && categoryDetailByCategoryName.size() > 0) {
                mUI.bindRightCatalogData(categoryDetailByCategoryName, categoryId, categoryIndexBean != null ? categoryIndexBean.categoryName : "", leftPosition);
            }
        }
    }

    /**
     * 加载缓存
     *
     * @param response response
     */
    public void loadCache(String response) {
        if (TextUtils.isEmpty(response)) {
            return;
        }
        requestData(response);
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
    }
}
