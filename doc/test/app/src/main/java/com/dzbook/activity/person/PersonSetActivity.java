package com.dzbook.activity.person;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.dzbook.AppConst;
import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.activity.CancelAutoOrderActivity;
import com.dzbook.activity.hw.RealNameAuthActivity;
import com.dzbook.activity.vip.AutoOrderVipActivity;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.push.HwPushHelper;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.ClearAppCacheUtil;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.SwitchButton;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.dzbook.view.person.PersonCommon2View;
import com.dzbook.view.person.PersonCommonView;
import com.dzbook.view.person.PersonSwitchView;
import com.ishugui.R;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 设置
 *
 * @author dongdianzhou on 2017/4/8.
 */
public class PersonSetActivity extends BaseSwipeBackActivity implements View.OnClickListener {

    private static final String TAG = "PersonSetActivity";

    private DianZhongCommonTitle mCommonTitle;
    private PersonCommon2View mCommonViewClearCache;
    private PersonCommonView mCommonViewRealName;
    private PersonCommonView mCommonViewReadPref;
    private PersonCommonView mCommonViewAutoOrderVIP;
    private PersonSwitchView mSkinViewEyeCareReadMode;
    private PersonSwitchView mSwitchViewMessage;
    private SpUtil spUtil;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personsystemset);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DeviceUtils.showBackGround(getActivity());

    }

    @Override
    protected void initData() {
        super.initData();
        spUtil = SpUtil.getinstance(getApplicationContext());
        if (spUtil.getIsReceiveMsg()) {
            mSwitchViewMessage.openSwitch();
        } else {
            mSwitchViewMessage.closedSwitch();
        }
        if (SettingManager.getInstance(this).getReaderEyeMode()) {
            mSkinViewEyeCareReadMode.openSwitch();
        } else {
            mSkinViewEyeCareReadMode.closedSwitch();
        }

        getCacheSize();
    }

    @Override
    protected void initView() {
        super.initView();
        mCommonTitle = findViewById(R.id.commontitle);
        mCommonViewReadPref = findViewById(R.id.commonview_readpref);
        mCommonViewClearCache = findViewById(R.id.personcommon2_clearcache);
        mCommonViewRealName = findViewById(R.id.commonview_real_name);
        mCommonViewAutoOrderVIP = findViewById(R.id.auto_order_vip_status);
        mSkinViewEyeCareReadMode = findViewById(R.id.skin_view_eye_care_read_mode);
        mSwitchViewMessage = findViewById(R.id.person_switch_view_message);

    }

    @Override
    protected void setListener() {
        super.setListener();
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.personcommonview_buy).setOnClickListener(this);
        mCommonViewReadPref.setOnClickListener(this);
        findViewById(R.id.personswitchview_plugin).setOnClickListener(this);
        mCommonViewAutoOrderVIP.setOnClickListener(this);
        mCommonViewRealName.setOnClickListener(this);
        mCommonViewClearCache.setOnClickListener(this);
        mSwitchViewMessage.mSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_RECEIVEMESSAGE_OPEN_VALUE, 1);
                } else {
                    ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_RECEIVEMESSAGE_CLOSED_VALUE, 1);
                }
                spUtil.setIsReceiveMsg(isChecked);
                HwPushHelper.getInstance().setReceiveNotifyMsg(isChecked);
            }
        });
        mSkinViewEyeCareReadMode.mSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onCheckedChanged(SwitchButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_READ_MODE_EYE_CARE_OPEN_VALUE, 1);
                    DzLog.getInstance().logClick(LogConstants.MODULE_XTSZ, LogConstants.ZONE_WD_HYMS, "2", null, null);
                } else {
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_READ_MODE_EYE_CARE_CLOSED_VALUE, 1);
                    DzLog.getInstance().logClick(LogConstants.MODULE_XTSZ, LogConstants.ZONE_WD_HYMS, "1", null, null);
                }
                SettingManager.getInstance(PersonSetActivity.this).setReaderEyeMode(isChecked);
                EventBusUtils.sendMessage(EventConstant.REQUESTCODE_EYE_MODE_CHANGE);
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (!NetworkUtils.getInstance().checkNet()) {
            switch (v.getId()) {
                case R.id.personcommon2_clearcache:
                    //清理缓存
                    ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_CLEARCANCEL_VALUE, 1);
                    showCleanCacheDialog();
                    break;
                //阅读偏好
                case R.id.commonview_readpref:
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_READPREF_VALUE, 1);
                    PersonReadPrefActivity.launch(getActivity());
                    break;
                default:
                    showNotNetDialog();
                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.personcommonview_buy:
                    //取消自动购买
                    ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_AUTOCANCELBUYNEXT_VALUE, 1);
                    Intent intent = new Intent(this, CancelAutoOrderActivity.class);
                    startActivity(intent);
                    showActivity(this);
                    break;
                case R.id.personcommon2_clearcache:
                    //清理缓存
                    ThirdPartyLog.onEventValueOldClick(getActivity(), ThirdPartyLog.PERSON_CENTER_SYSTEMSET_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_SYSTEMSET_CLEARCANCEL_VALUE, 1);
                    showCleanCacheDialog();
                    break;
                //阅读偏好
                case R.id.commonview_readpref:
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.PERSON_CENTER_MENU_UMENG_ID, ThirdPartyLog.PERSON_CENTER_READPREF_VALUE, 1);
                    PersonReadPrefActivity.launch(getActivity());
                    break;
                case R.id.personswitchview_plugin:
                    PersonPluginActivity.launch(this);
                    break;
                case R.id.commonview_real_name:
                    RealNameAuthActivity.launch(getContext());
                    break;
                case R.id.auto_order_vip_status:
                    LoginUtils.getInstance().forceLoginCheck(PersonSetActivity.this, new LoginUtils.LoginCheckListener() {
                        @Override
                        public void loginComplete() {
                            AutoOrderVipActivity.launch(getActivity());
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void getCacheSize() {

        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {

            @Override
            public void subscribe(ObservableEmitter<Long> e) {
                long cacheSize = 0;
                File extFile = AppConst.getGlideCacheFile();
                File[] listFiles = FileUtils.getListFile(extFile);
                if (listFiles != null && listFiles.length > 0) {
                    for (File child : listFiles) {
                        if (child.exists() && child.isFile()) {
                            cacheSize = cacheSize + child.length();
                        }
                    }
                }

                e.onNext(cacheSize);
                e.onComplete();
            }
        });

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Long>() {

                    @Override
                    public void onNext(Long result) {

                        //转换文件大小
                        String size = FileUtils.formatFileSize(result);
                        if (TextUtils.isEmpty(size)) {
                            mCommonViewClearCache.setContentVisible(View.GONE);
                        } else {
                            mCommonViewClearCache.setContentText(size);
                            mCommonViewClearCache.setContentVisible(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
    }


    /**
     * 打开清空缓存dialog
     */
    public void showCleanCacheDialog() {
        CustomHintDialog customHintDialog = new CustomHintDialog(this, 1);
        customHintDialog.setDesc(getResources().getString(R.string.dialog_clean_cache));
        customHintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                DzSchedulers.execute(new Runnable() {
                    @Override
                    public void run() {
                        ClearAppCacheUtil.deleteFilesByDir(PersonSetActivity.this.getCacheDir());
                        ClearAppCacheUtil.deleteFilesByDir(new File("/data/data/" + PersonSetActivity.this.getPackageName()
                                + "/app_webview"));
                        Glide.get(getContext()).clearDiskCache();
                        getCacheSize();
                    }
                });
            }

            @Override
            public void clickCancel() {

            }
        });
        customHintDialog.setConfirmTxt(getResources().getString(R.string.dialog_clean_btn_enter));
        customHintDialog.show();
    }
}
