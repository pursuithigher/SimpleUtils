package com.dzbook.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.fragment.main.MainShelfFragment;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.RechargeListPresenter;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.order.RechargeParamBean;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.common.dialog.base.CustomDialogParent;
import com.dzbook.web.ActionEngine;
import com.dzpay.recharge.bean.RechargeAction;
import com.ishugui.R;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import hw.sdk.net.bean.shelf.BeanShelfActivityInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * DialogBookShelfActivity
 */
public class DialogBookShelfActivity extends CustomDialogParent implements android.view.View.OnClickListener {
    private static final String CLICK_ACTION_BOOK = "1";
    private static final String CLICK_ACTION_URL = "10";
    private static final String CLICK_ACTION_RECHARGE = "12";

    private ImageView imageViewActivity;
    private ImageView imageviewClose;
    private Activity context;
    private BeanShelfActivityInfo bean;

    private CompositeDisposable composite = new CompositeDisposable();


    /**
     * 构造
     *
     * @param context context
     */
    public DialogBookShelfActivity(Activity context) {
        super(context, R.style.dialog_normal);
        this.context = context;
        setContentView(R.layout.dialog_bookshelf_activity);
        setProperty(1, 1);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        imageViewActivity = this.findViewById(R.id.imageView_activity);
        imageviewClose = this.findViewById(R.id.imageview_close);
    }

    private void initData() {
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
    }

    /**
     * 显示
     *
     * @param activityBean activityBean
     */
    public void show(BeanShelfActivityInfo activityBean) {
        boolean flag = false;
        if (activityBean != null && !TextUtils.isEmpty(activityBean.imageUrl)) {
            flag = SpUtil.getinstance(context).getBoolean(activityBean.imageUrl);
        }

        bean = activityBean;
        if (!flag) {
            if (activityBean != null && !TextUtils.isEmpty(activityBean.imageUrl)) {

                Long currentTime = System.currentTimeMillis();
                Long nextTime = SpUtil.getinstance(context).getLong(bean.imageUrl + "nexttime", 0L);

                if (currentTime > nextTime) {
                    GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(context, activityBean.imageUrl, new GlideImageLoadUtils.DownloadImageListener() {
                        @Override
                        public void downloadSuccess(Bitmap bitmap) {
                            imageViewActivity.setImageBitmap(bitmap);

                            try {

                                if (context != null && !context.isFinishing()) {
                                    show();
                                }

                            } catch (Exception e) {
                                ALog.printStackTrace(e);
                            }

                            Disposable disposable = AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    if (DialogBookShelfActivity.this.isShowing()) {
                                        DialogBookShelfActivity.this.dismiss();
                                    }
                                }
                            }, 30000, TimeUnit.MILLISECONDS);
                            composite.addAndDisposeOldByKey("delayDismissDialog", disposable);
                        }


                        @Override
                        public void downloadSuccess(File resource) {
                        }

                        @Override
                        public void downloadFailed() {
                        }
                    }, true);
                }

            }

        }
    }

    @Override
    public void show() {
        super.show();
    }

    private void setListener() {
        imageviewClose.setOnClickListener(this);
        imageViewActivity.setOnClickListener(this);
        final long num = 24L;
        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (bean != null && !TextUtils.isEmpty(bean.imageUrl)) {
                    // 设置下次跑马灯活动页显示时间
                    SpUtil.getinstance(context).setLong(bean.imageUrl + "nexttime", System.currentTimeMillis() + num * 3600000);
                }
            }
        });
    }

    @Override
    public void dismiss() {
        if (composite != null) {
            composite.disposeAll();
        }
        super.dismiss();
    }

    /**
     * type为1时候 noticeType代表跳转的页面 1:书籍详情,解析strId(书籍id)
     * 10：url类型
     * 12：充值
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageview_close) {
            onClickClose();
        } else if (id == R.id.imageView_activity) {
            onClickActive();
        }
    }

    private void onClickActive() {
        String type = "";
        if (bean != null && !TextUtils.isEmpty(bean.type)) {
            type = bean.type;
        }
        if (!TextUtils.isEmpty(type)) {
            if (CLICK_ACTION_BOOK.equals(type)) {
                openBook();
            } else if (CLICK_ACTION_URL.equals(type)) {
                if (openUrl()) {
                    return;
                }
            } else if (CLICK_ACTION_RECHARGE.equals(type)) {
                toRecharge();
            }
        }
        if (bean != null && !TextUtils.isEmpty(bean.imageUrl)) {
            SpUtil.getinstance(context).setBoolean(bean.imageUrl, true);
        }
        if (bean != null) {
            HashMap<String, String> map = new HashMap<>(16);
            map.put("is_recharge", "12".equals(bean.type) ? 1 + "" : 2 + "");
            DzLog.getInstance().logClick(LogConstants.MODULE_SJ, LogConstants.ZONE_SJ_SJTCHD, bean.type, map, "");
        }
        dismiss();
    }

    private void onClickClose() {
        if (bean != null && !TextUtils.isEmpty(bean.imageUrl)) {
            SpUtil.getinstance(context).setBoolean(bean.imageUrl, true);
        }
        dismiss();
    }

    private void openBook() {
        String bookId = bean.resourceId;
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra("bookId", bookId);
        ThirdPartyLog.onEventValueOldClick(context, ThirdPartyLog.ACTIVITY_UMENG_ID, ThirdPartyLog.RECOMMEND_BOOK_DIALOG_VALUE, 1);
        context.startActivity(intent);
        BookDetailActivity.showActivity(context);
    }

    private boolean openUrl() {
        // FIXMEDongdz: 2018/4/13 此处打点没有细分
        Intent intent = ActionEngine.getInstance().jsonToIntent(getContext(), ActionEngine.MODE_URL, bean.url, bean.title, ActionEngine.FROM_SC, bean.type);
        if (null == intent) {
            return true;
        }
        getContext().startActivity(intent);
        return false;
    }

    private void toRecharge() {
        Listener listener = new Listener() {
            @Override
            public void onSuccess(int ordinal, HashMap<String, String> parm) {

            }

            @Override
            public void onFail(HashMap<String, String> parm) {

            }
        };

        RechargeParamBean paramBean = new RechargeParamBean(context, listener, RechargeAction.RECHARGE.ordinal(), "书架活动", null, null, MainShelfFragment.TAG, LogConstants.RECHARGE_SOURCE_FROM_VALUE_4);
        RechargeListPresenter.launch(paramBean);
    }

}
