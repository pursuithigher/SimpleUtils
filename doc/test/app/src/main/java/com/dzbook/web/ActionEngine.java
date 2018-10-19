package com.dzbook.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;

import com.dzbook.BaseLoadActivity;
import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.store.LimitFreeActivity;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.listener.AddBookListener;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.service.InsertBookInfoDataUtil;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.bookDetail.BeanBookDetail;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * ActionEngine
 *
 * @author caimantang on 2017/8/18.
 */
public class ActionEngine {

    /**
     * 来自书书架活动
     */
    public static final String FROM_SC = "54";

    /**
     * 个推透传
     */
    public static final String GTTC = "1011";
    /**
     * 书架签到
     */
    public static final String SJ_QD = "1012";
    /**
     * webmanager 活动中心（type=4）
     */
    public static final String WEB_HDZX = "1021";
    /**
     * 书城精选
     */
    public static final String SC_JX = "1022";
    /**
     * 书城男频
     */
    public static final String SC_NP = "1023";
    /**
     * 书城分类
     */
    public static final String SC_FL = "1024";
    /**
     * 书城活动
     */
    public static final String SC_HD = "1025";
    /**
     * 书城女频
     */
    public static final String SC_NV_P = "1026";
    /**
     * 书城优惠尝鲜
     */
    public static final String SC_YHCX = "1027";
    /**
     * 书城免费
     */
    public static final String SC_MF = "1028";
    /**
     * 书城测试专用
     */
    public static final String SC_CSZY = "1029";

    /**
     * 书城新手礼包
     */
    public static final String SC_XSLB = "1030";

    /**
     * 推荐页面精选
     */
    public static final String TJ_JX = "1032";
    /**
     * 推荐页面男频
     */
    public static final String TJ_NP = "1033";
    /**
     * 推荐页面分类
     */
    public static final String TJ_FL = "1034";
    /**
     * 推荐页面活动
     */
    public static final String TJ_HD = "1035";
    /**
     * 推荐页面女频
     */
    public static final String TJ_NV_P = "1036";
    /**
     * 推荐页面优惠尝鲜
     */
    public static final String TJ_YHCX = "1037";
    /**
     * 推荐页面免费
     */
    public static final String TJ_MF = "1038";
    /**
     * 2
     */
    public static final int MODE_URL = 2;
    private static final int MODE_JS = 1;
    /**
     * url pType
     */
    private static final String[] URL_PAY = {

    };
    /**
     * js pType
     */
    private static String[] jsPay = {
            "01",//签到管理
            "04"//奖品中心
    };
    private static volatile ActionEngine instance;

    private ActionEngine() {
    }

    /**
     * 获取单例
     *
     * @return 实例
     */
    public static ActionEngine getInstance() {
        if (null == instance) {
            synchronized (ActionEngine.class) {
                if (null == instance) {
                    instance = new ActionEngine();
                }
            }
        }
        return instance;
    }

    /**
     * 去免费
     *
     * @param activity activity
     */
    public void toFree(Activity activity) {
//        String url = StringUtil.putUrlValue(RequestCall.getLimitFree(), "readPref", SpUtil.getinstance(activity).getPersonReadPref() + "");
//        CenterDetailActivity.show(activity, url, "限免");
//        SpecialAreaActivity.launch(activity, activity.getResources().getString(R.string.shelf_free_zone));
        LimitFreeActivity.launch(activity);
        //test
//        CenterDetailActivity.show(activity, "http://192.168.0.60:3080/php/batch/index");
    }

    /**
     * 添加图书到书架
     *
     * @param mActivity       mActivity
     * @param bookId          bookId
     * @param ghType          ghType
     * @param addBookListener addBookListener
     */
    public void addBookShelf(final BaseLoadActivity mActivity, final String bookId, final String ghType, final AddBookListener addBookListener) {
        ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.PERSON_CENTER_CLOUDSELF_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_CLOUDSELF_ADDSELF_VALUE, 1);
        if (!NetworkUtils.getInstance().checkNet()) {
            if (mActivity != null) {
                mActivity.showNotNetDialog();
            }
            return;
        }
        mActivity.showDialogByType(DialogConstants.TYPE_GET_DATA);
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
                    ArrayList<BeanChapterInfo> chapterList = bookDetailBean.chapters;
                    if (ListUtils.isEmpty(chapterList)) {
                        e.onError(new Exception(""));
                        return;
                    }
                    InsertBookInfoDataUtil.appendBookAndChapters(mActivity, chapterList,
                            beanBookInfo, true, null);

                    BookInfo mBookInfo = new BookInfo();
                    mBookInfo.bookid = bookId;
                    mBookInfo.hasRead = 2;
                    mBookInfo.isAddBook = 2;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = WhiteListWorker.setPnPi(mActivity, jsonObject);
                    try {
                        jsonObject.put(LogConstants.GH_TYPE, ghType);
                        String readerFrom = jsonObject.toString();
                        if (!TextUtils.isEmpty(readerFrom)) {
                            mBookInfo.readerFrom = readerFrom;
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    DBUtils.updateBook(mActivity, mBookInfo);
                    e.onNext("");
                } else {
                    e.onError(new Exception(""));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String value) {
                        mActivity.dissMissDialog();
//                        ToastAlone.showShort(mActivity.getResources().getString(R.string.add_bookshelf_success));
                        if (null != addBookListener) {
                            addBookListener.success();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.dissMissDialog();
                        ToastAlone.showShort(mActivity.getResources().getString(R.string.add_book_shelf_fail_retry));
                        if (null != addBookListener) {
                            addBookListener.fail();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 新手礼包
     *
     * @param context context context
     */
    public void toNewGift(final Activity context) {
        //签到页需要检查登录
        LoginUtils loginUtils = LoginUtils.getInstance();
        loginUtils.forceLoginCheck(context, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                DzLog.getInstance().logClick(LogConstants.MODULE_SC, LogConstants.ZONE_NSC_XSLB, null, null, null);
                ThirdPartyLog.onEventValueOldClick(context, ThirdPartyLog.BOOK_STORE_NEWGIFT_ID, null, 1);
                Intent intent = new Intent(context, CenterDetailActivity.class);
                intent.putExtra("url", RequestCall.urlNewGift());
                intent.putExtra("notiTitle", "新手礼包");
                intent.putExtra("web", SC_XSLB);
                HashMap<String, String> priMap = new HashMap<String, String>();
                intent.putExtra("priMap", priMap);
                context.startActivity(intent);
                BaseActivity.showActivity(context);
            }
        });
    }

    /**
     * 签到
     *
     * @param context context context
     */
    public void toSign(final Activity context) {
        //打点
        DzLog.getInstance().logClick(LogConstants.MODULE_SJ, LogConstants.ZONE_SJ_QD, null, null, null);
        //签到页需要检查登录
        LoginUtils loginUtils = LoginUtils.getInstance();
        loginUtils.forceLoginCheck(context, new LoginUtils.LoginCheckListener() {
            @Override
            public void loginComplete() {
                ThirdPartyLog.onEventValueOldClick(context, ThirdPartyLog.BOOK_SHELF_SIGN_UMENG_ID, null, 1);
                Intent intent = new Intent(context, CenterDetailActivity.class);
                intent.putExtra("url", RequestCall.urlSignIn());
                intent.putExtra("notiTitle", "签到");
                intent.putExtra("isReload", true);
                intent.putExtra("web", SJ_QD);
                HashMap<String, String> priMap = new HashMap<String, String>();
                intent.putExtra("priMap", priMap);
                context.startActivity(intent);
                BaseActivity.showActivity(context);
            }
        });
    }

    /**
     * H5添加图书
     *
     * @param context     context
     * @param action      action
     * @param commodityId commodityId
     * @param originate   originate
     * @param bookIds     bookIds
     * @param descFrom    descFrom
     * @param mWebView    mWebView
     * @param trackId     trackId
     * @param webFrom     webFrom
     * @param isNeedLogin isNeedLogin
     */
    public static void addBookByH5(final Context context, final String action, final String commodityId, final String originate,
                                   final String bookIds, final String descFrom, final WebView mWebView, final String trackId, final String webFrom, boolean isNeedLogin) {
        if (isNeedLogin) {
            LoginUtils.getInstance().forceLoginCheck(context, new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    WebViewJsUtils.packPrice(context, action, commodityId, originate, bookIds, descFrom, mWebView, trackId, webFrom);
                }
            });
        } else {
            WebViewJsUtils.packPrice(context, action, commodityId, originate, bookIds, descFrom, mWebView, trackId, webFrom);
        }
    }

    /**
     * json 转换为 intent
     *
     * @param context context
     * @param mode    方式js or url
     * @param url     url
     * @param title   活动标题
     * @param from    来源
     * @param pType   页面类型
     * @return intent
     */
    public Intent jsonToIntent(Context context, int mode, String url, String title, String from, String pType) {
        Intent intent = null;
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (isNeedLogin(mode, pType)) {
            if (TextUtils.isEmpty(SpUtil.getinstance(context).getUserID())) {
                ToastAlone.showShort(R.string.toast_need_login);
                return null;
            }
        }
        intent = new Intent(context, CenterDetailActivity.class);
        if (!TextUtils.isEmpty(from)) {
            intent.putExtra("web", from + (TextUtils.isEmpty(pType) ? "00" : pType));
        }
        intent.putExtra("p_type", pType);
        intent.putExtra("mode", mode);
        intent.putExtra("url", url);
        intent.putExtra("need_param", "1");
        intent.putExtra("notiTitle", title);
        HashMap<String, String> hashMap = new HashMap<>();
        intent.putExtra("priMap", hashMap);
        return intent;
    }

    private boolean isNeedLogin(int mode, String pType) {
        if (TextUtils.isEmpty(pType)) {
            return false;
        }
        String[] strings = null;
        switch (mode) {
            case MODE_JS:
                strings = jsPay;
                break;
            case MODE_URL:
                strings = URL_PAY;
                break;
            default:
                break;
        }
        if (null == strings || strings.length <= 0) {
            return false;
        }
        for (String string : strings) {
            if (string.equals(pType)) {
                return true;
            }
        }
        return false;
    }
}
