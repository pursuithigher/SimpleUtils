package com.dzbook.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.dzbook.dialog.DianzhongCommonDialog;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.type.DownloadEvent;
import com.dzbook.filedownloader.BaseDownloadTask;
import com.dzbook.filedownloader.FileDownloadListener;
import com.dzbook.filedownloader.FileDownloader;
import com.dzbook.filedownloader.connection.FileDownloadUrlConnection;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.io.File;
import java.net.Proxy;
import java.util.HashMap;

/**
 * ManagerUtils
 *
 * @author dongdianzhou on 2017/12/8.
 */

public class NewDownloadManagerUtils {
    private static volatile NewDownloadManagerUtils instanse;

    /**
     * 上下文对象
     */
    private Context mContext;
    /**
     * 相同的连接1分钟以内触发一次下载
     */
    private HashMap<String, Long> map;

    private FileDownloadListener mPluginListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            DownloadEvent event = new DownloadEvent(DownloadEvent.STATE_PENDING, soFarBytes, totalBytes, task.getUrl(), task.getPath());
            EventBusUtils.sendMessage(event);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            DownloadEvent event = new DownloadEvent(DownloadEvent.STATE_PROGRESS, soFarBytes, totalBytes, task.getUrl(), task.getPath());
            EventBusUtils.sendMessage(event);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            DownloadEvent event = new DownloadEvent(DownloadEvent.STATE_COMPLETED, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes(), task.getUrl(), task.getPath());
            EventBusUtils.sendMessage(event);
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            DownloadEvent event = new DownloadEvent(DownloadEvent.STATE_PAUSED, soFarBytes, totalBytes, task.getUrl(), task.getPath());
            EventBusUtils.sendMessage(event);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            DownloadEvent event = new DownloadEvent(DownloadEvent.STATE_ERROR, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes(), task.getUrl(), task.getPath());
            EventBusUtils.sendMessage(event);
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            DownloadEvent event = new DownloadEvent(DownloadEvent.STATE_WARN, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes(), task.getUrl(), task.getPath());
            EventBusUtils.sendMessage(event);
        }
    };

    private NewDownloadManagerUtils() {
        map = new HashMap<>();
    }

    /**
     * 初始化下载连接
     *
     * @param application application
     * @param context     context
     */
    public void initDownConnect(Application application, Context context) {
        mContext = context;
        FileDownloader.setupOnApplicationOnCreate(application).connectionCreator(new FileDownloadUrlConnection.Creator(new FileDownloadUrlConnection.Configuration().connectTimeout(30 * 1000).readTimeout(30 * 1000).proxy(Proxy.NO_PROXY))).commit();
    }

    /**
     * 获取NewDownloadManagerUtils实例
     *
     * @return 实例
     */
    public static NewDownloadManagerUtils getInstanse() {
        if (instanse == null) {
            synchronized (NewDownloadManagerUtils.class) {
                if (instanse == null) {
                    instanse = new NewDownloadManagerUtils();
                }
            }
        }
        return instanse;
    }

    /**
     * 下载文件（如果管理外面可以通过tag（url）获取下载信息）
     *
     * @param url           url
     * @param savePath      ：全路径
     * @param isApk         ：是否apk：目前活动都是，默认为TRUE即可，下载前和下载后椰汁处理了apk，如果其他类型处理徐修改下面几个地方： 1. js：type=27
     *                      3. webview下载监听
     * @param contentLength 文件长度：byte为单位
     * @param activity      activity
     */
    public void down(Activity activity, final String url, final String savePath, final boolean isApk, long contentLength) {
        long lastTime = 0;
        if (map.get(url) != null) {
            lastTime = map.get(url);
        }
        long currentTime = System.currentTimeMillis();
        //相同的url，3s内不处理
        if (currentTime - lastTime < 1000 * 3) {
            return;
        }
        map.put(url, currentTime);
        if (contentLength >= (FileUtils.getSDFreeSize() * 1024 * 1024)) {
            ToastAlone.showShort(mContext.getString(R.string.downoutexception));
            return;
        }
        //大于10M
        if (contentLength != 0 && contentLength > (1024 * 1024 * 10)) {
            boolean isWifi = NetworkUtils.getInstance().checkWiFi();
            if (!isWifi) {
                DianzhongCommonDialog dialog = new DianzhongCommonDialog(activity);
                dialog.show(mContext.getString(R.string.down_tip_content), mContext.getString(R.string.down_ok), mContext.getString(R.string.down_cancel), new DianzhongCommonDialog.DialogClickAction() {
                    @Override
                    public void okAction() {
                        startDown(url, savePath, isApk);
                    }

                    @Override
                    public void cancelAction() {

                    }
                });
                return;
            }
            startDown(url, savePath, isApk);
        } else {
            startDown(url, savePath, isApk);
        }
    }

    /**
     * 启动下载
     *
     * @param url
     * @param savePath
     * @param isApk
     */
    private void startDown(final String url, final String savePath, final boolean isApk) {
        FileDownloader.getImpl().create(url).setPath(savePath, false).setCallbackProgressTimes(100).setAutoRetryTimes(3)//下载失败设置其自动重试3次
                .setTag(url).setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void started(BaseDownloadTask task) {
                super.started(task);
                //                        int total = task.getSmallFileTotalBytes();
                //                        ALog.eDongdz("**********************启动下载started:**********************:total:" + total);
                ToastAlone.showShort(mContext.getString(R.string.downstart));
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                //                        ALog.eDongdz("**********************下载中completed:**********************");
                String tag = (String) task.getTag();
                //下载进度和当前下载匹配起来
                if (!TextUtils.isEmpty(tag) && url.equals(tag)) {
                    File file = new File(savePath);
                    //下面的通知点击跳转apk安装，所以加上这层校验
                    if (isApk) {
                        PluginUtils.installFile(mContext, file);
                    }
                    //                            ToastAlone.showShort(mContext.getString(R.string.downsuccess));
                }
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                //                        ALog.eDongdz("**********************下载中paused:**********************");

            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                //                        ALog.eDongdz("**********************下载中error:**********************" + e.getMessage());
                ToastAlone.showShort(mContext.getString(R.string.downhttpexception));

            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        }).start();
    }


    /**
     * 下载插件
     *
     * @param url      url
     * @param savePath savePath
     */
    public void downPlugin(final String url, final String savePath) {
        FileDownloader.getImpl().create(url).setPath(savePath).setCallbackProgressTimes(100).setAutoRetryTimes(3).setTag(url).setListener(mPluginListener).start();
    }
}
