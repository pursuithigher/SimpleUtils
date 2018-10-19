package com.dzbook.mvp.presenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.database.bean.PluginInfo;
import com.dzbook.event.EventBus;
import com.dzbook.event.type.DownloadEvent;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.PersonPluginUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.r.util.ZipUtils;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.NewDownloadManagerUtils;
import com.dzbook.utils.PluginUtils;
import com.dzbook.utils.UtilApkCheck;
import com.dzbook.utils.WpsModel;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import org.json.JSONObject;

import java.io.File;

import hw.sdk.net.bean.tts.PluginTtsInfo;
import hw.sdk.net.bean.tts.PluginWpsInfo;
import hw.sdk.net.bean.tts.Plugins;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 插件
 *
 * @author wxliao 18/1/17
 */

public class PersonPluginPresenter {
    /**
     * 下载
     */
    public String textFreeLoad;
    /**
     * 更新
     */
    public String textUpdate;
    /**
     * 下载中
     */
    public String textLoading;
    /**
     * 请安装
     */
    public String textInstall;
    /**
     * 已安装
     */
    public String textInstalled;
    /**
     * 暂不支持
     */
    public String textDisable;

    protected CompositeDisposable composite = new CompositeDisposable();
    private PersonPluginUI mUI;
    private String ttsFolder = "/tts";

    private PluginTtsInfo netTtsInfo;
    private PluginTtsInfo dbTtsInfo;

    private PluginWpsInfo netWpsInfo;

    /**
     * 构造
     *
     * @param ui ui
     */
    public PersonPluginPresenter(PersonPluginUI ui) {
        mUI = ui;
        create();
        Resources resources = AppConst.getApp().getResources();
        textFreeLoad = resources.getString(R.string.str_free_down);
        textUpdate = resources.getString(R.string.str_book_gx);
        textLoading = resources.getString(R.string.str_downling);
        textInstall = resources.getString(R.string.str_please_install);
        textInstalled = resources.getString(R.string.str_also_install);
        textDisable = resources.getString(R.string.str_plugin_disable);
    }

    /**
     * 注册eventsBus
     */
    public void create() {
        EventBus.getDefault().register(this);
    }

    /**
     * destroy
     */
    public void destroy() {
        EventBus.getDefault().unregister(this);
        composite.disposeAll();
    }


    /**
     * 操作UI
     *
     * @param event event
     */
    public void onEventMainThread(DownloadEvent event) {
        String url = event.downloadUrl;
        if (netTtsInfo != null && TextUtils.equals(url, netTtsInfo.zipUrl)) {
            if (event.state == DownloadEvent.STATE_PROGRESS) {
                int progress = (int) (event.downloadSize * (long) 100 / event.totalSize);
                mUI.showTtsItem(progress, textLoading);
            } else if (event.state == DownloadEvent.STATE_COMPLETED) {
                unZipTts(event.savePath);
                DzLog.getInstance().logEvent(LogConstants.EVENT_CJGL_TTS_A, null, null);
            }
        } else if (netWpsInfo != null && TextUtils.equals(url, netWpsInfo.downloadUrl)) {
            if (event.state == DownloadEvent.STATE_PROGRESS) {
                int progress = (int) (event.downloadSize * (long) 100 / event.totalSize);
                mUI.showWpsItem(progress, textLoading);
            } else if (event.state == DownloadEvent.STATE_COMPLETED) {
                if (fileIsExists(getSavePath()) && !(UtilApkCheck.isInstalledApp(mUI.getContext(), WpsModel.PACKAGENAME_NORMAL))) {
                    PluginUtils.installFile(mUI.getContext(), new File(event.savePath));
                } else if (UtilApkCheck.isInstalledApp(mUI.getContext(), WpsModel.PACKAGENAME_NORMAL)) {
                    mUI.showWpsItem(0, textInstalled);
                }
            }
        }
    }

    /**
     * 获取数据
     *
     * @param toInstallPlugin toInstallPlugin
     */
    public void getData(final String toInstallPlugin) {
        Single.create(new SingleOnSubscribe<Plugins>() {

            @Override
            public void subscribe(SingleEmitter<Plugins> e) throws Exception {
                Plugins plugins = HwRequestLib.getInstance().getPluginInfo();
                e.onSuccess(plugins);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<Plugins>() {
            @Override
            public void onSubscribe(Disposable d) {
                composite.addAndDisposeOldByKey("getData", d);
                mUI.setStatusViewType(1);
            }

            @Override
            public void onSuccess(Plugins value) {
                mUI.setStatusViewType(0);
                if (value.isSuccess()) {
                    if (value.ttsPlugin != null && value.ttsPlugin.isEnable()) {
                        netTtsInfo = value.ttsPlugin.ttsInfo;
                        checkTtsInfo(netTtsInfo);
                    } else {
                        mUI.showTtsItem(0, textDisable);
                    }

                    if (value.wpsPlugin != null && value.wpsPlugin.isEnable()) {
                        netWpsInfo = value.wpsPlugin.wpsInfo;
                        checkWpsInfo(netWpsInfo);
                    } else {
                        mUI.showWpsItem(0, textDisable);
                    }
                }

                if (TextUtils.equals(toInstallPlugin, PluginInfo.TTS_NAME)) {
                    loadTts();
                } else if (TextUtils.equals(toInstallPlugin, PluginInfo.WPS_NAME)) {
                    loadWps();
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.setStatusViewType(2);
            }
        });


    }

    /**
     * 刷新wps信息
     */
    public void refreshWpsInfo() {
        checkWpsInfo(netWpsInfo);
    }

    private void checkWpsInfo(PluginWpsInfo wpsInfo) {
        if (wpsInfo == null) {
            return;
        }
        PackageInfo packageInfo = PluginUtils.getInstalledWpsInfo(mUI.getContext());
        if (packageInfo != null) {
            //已经安装插件
            if (TextUtils.equals(packageInfo.packageName, wpsInfo.packageName)) {
                mUI.showWpsItem(0, textInstalled);
            } else {
                //安装的插件与服务器的不一致，不做更新检查
                mUI.showWpsItem(0, textInstalled);
            }
        } else if (fileIsExists(getSavePath()) && !(UtilApkCheck.isInstalledApp(mUI.getContext(), WpsModel.PACKAGENAME_NORMAL))) {
            mUI.showWpsItem(0, textInstall);
        } else {
            mUI.showWpsItem(0, textFreeLoad);
        }
    }

    private void checkTtsInfo(PluginTtsInfo ttsInfo) {
        if (ttsInfo == null) {
            return;
        }
        PluginInfo pluginInfo = DBUtils.getPlugin(mUI.getContext(), PluginInfo.TTS_NAME);
        if (pluginInfo != null) {
            dbTtsInfo = pluginInfo.getTtsInfo();
        }
        if (dbTtsInfo == null) {
            mUI.showTtsItem(0, textFreeLoad);
            return;
        }
        //已安装，有更新
        if (ttsInfo.version > dbTtsInfo.version) {
            mUI.showTtsItem(0, textUpdate);
        } else {
            //已安装，无更新
            mUI.showTtsItem(0, textInstalled);
            //无更新的情况，也刷新一下数据，以防appId等填错的情况
            dbTtsInfo.appId = netTtsInfo.appId;
            dbTtsInfo.appKey = netTtsInfo.appKey;
            dbTtsInfo.secretKey = netTtsInfo.secretKey;
            dbTtsInfo.updateTime = System.currentTimeMillis();
            updateDbTts(mUI.getContext(), dbTtsInfo);
        }
    }

    /**
     * 添加插件
     *
     * @param context context
     * @param ttsInfo ttsInfo
     * @return boolean
     */
    public static boolean updateDbTts(Context context, PluginTtsInfo ttsInfo) {
        JSONObject jsonObject = ttsInfo.toJSON();
        if (jsonObject != null) {
            PluginInfo info = new PluginInfo();
            info.name = PluginInfo.TTS_NAME;
            info.info = jsonObject.toString();
            DBUtils.addPlugin(context, info);
            return true;
        }
        return false;
    }

    private void loadTts() {
        if (netTtsInfo == null) {
            return;
        }
        File file = mUI.getContext().getFilesDir();
        File ttsDir = new File(file, ttsFolder);
        if (!ttsDir.exists()) {
            if (ttsDir.mkdir()) {
                ALog.dWz("mkdir success  " + file);
            }
        }
        String fileName = netTtsInfo.zipUrl.hashCode() + ".zip";
        String savePath = ttsDir.getAbsolutePath() + File.separator + fileName;
        NewDownloadManagerUtils.getInstanse().downPlugin(netTtsInfo.zipUrl, savePath);
    }

    private void loadWps() {
        if (netWpsInfo == null) {
            return;
        }
        String dirStr = SDCardUtil.getInstance().getSDCardAndroidRootDir() + File.separator + FileUtils.APP_DOWNLOAD;
        File dir = new File(dirStr);
        if (!dir.exists() && !dir.mkdir()) {
            ALog.eLk("loadWps mkdir error");
        }
        String fileName = netWpsInfo.downloadUrl.hashCode() + ".apk";
        String savePath = dir.getAbsolutePath() + File.separator + fileName;
        NewDownloadManagerUtils.getInstanse().downPlugin(netWpsInfo.downloadUrl, savePath);
    }

    private void unZipTts(final String savePath) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<PluginTtsInfo>() {
            @Override
            public void subscribe(ObservableEmitter<PluginTtsInfo> e) throws Exception {
                File file = new File(savePath);
                ZipUtils.unzipFile(file, file.getParentFile());
                if (netTtsInfo != null) {
                    PluginTtsInfo saveTtsInfo = new PluginTtsInfo(netTtsInfo);
                    saveTtsInfo.cachePath = file.getParent();
                    saveTtsInfo.updateTime = System.currentTimeMillis();
                    boolean result = updateDbTts(mUI.getContext(), saveTtsInfo);
                    if (result) {
                        DzLog.getInstance().logEvent(LogConstants.EVENT_CJGL_TTS_B, null, null);
                        e.onNext(saveTtsInfo);
                        e.onComplete();
                        return;
                    }
                }
                e.onError(new RuntimeException("unzip failed"));

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<PluginTtsInfo>() {
            @Override
            public void onNext(PluginTtsInfo value) {
                dbTtsInfo = value;
                mUI.showTtsItem(0, textInstalled);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        composite.addAndDisposeOldByKey("unZipTts", disposable);
    }

    /**
     * 判断文件是否存在
     *
     * @param strFile
     * @return
     */
    private boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private String getSavePath() {
        String dirStr = SDCardUtil.getInstance().getSDCardAndroidRootDir() + File.separator + FileUtils.APP_DOWNLOAD;
        File dir = new File(dirStr);
        if (!dir.exists() && !dir.mkdir()) {
            ALog.eLk("getSavePath mkdir error");
        }
        String fileName = netWpsInfo.downloadUrl.hashCode() + ".apk";
        return dir.getAbsolutePath() + File.separator + fileName;
    }

    /**
     * 无网络弹窗
     *
     * @param pType pType
     */
    public void showNotWifiDialog(int pType) {
        if (NetworkUtils.NETWORK_NONE == NetworkUtils.getInstance().getNetworkState()) {
            showNotNetDialog();
        } else if (NetworkUtils.NETWORK_WIFI != NetworkUtils.getInstance().getNetworkState()) {
            notWifiDialog(pType);
        } else {
            if (0 == pType) {
                loadTts();
            } else if (1 == pType) {
                loadWps();
            }
        }
    }

    /**
     * isDownLoadEd
     *
     * @param pType pType
     */
    public void isDownLoadEd(int pType) {
        if (0 == pType) {
            loadTts();
        } else if (1 == pType) {
            loadWps();
        }
    }

    private void notWifiDialog(final int mType) {
        CustomHintDialog customHintDialog = new CustomHintDialog(mUI.getContext(), 1);
        customHintDialog.setDesc(mUI.getContext().getResources().getString(R.string.str_not_wifi_prompt));
        customHintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                if (0 == mType) {
                    loadTts();
                } else if (1 == mType) {
                    loadWps();
                }
            }

            @Override
            public void clickCancel() {

            }
        });
        customHintDialog.setConfirmTxt(mUI.getContext().getResources().getString(R.string.down_ok));
        customHintDialog.show();
    }

    private void showNotNetDialog() {
        if (mUI.getContext() instanceof BaseActivity) {
            ((BaseActivity) mUI.getContext()).showNotNetDialog();
        }
    }
}
