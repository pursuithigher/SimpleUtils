package com.dzbook.loader;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.lib.utils.UtilTimeOffset;
import com.dzbook.model.ModelAction;
import com.dzbook.net.hw.HwRequest;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.pay.Listener;
import com.dzbook.recharge.RechargeObserver;
import com.dzbook.service.MarketDao;
import com.dzbook.service.RechargeParams;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.PackageControlUtils;
import com.dzbook.utils.SpUtil;
import com.dzpay.recharge.api.UtilRecharge;
import com.dzpay.recharge.bean.RechargeAction;
import com.dzpay.recharge.bean.RechargeMsgResult;
import com.dzpay.recharge.utils.RechargeMsgUtils;
import com.ishugui.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;


/**
 * 自有加载
 *
 * @author wxliao on 17/8/2.
 */
public class LoaderForDz extends BaseLoader {
    private static final String TAG = "LoaderForDz";

    /**
     * Same as Okio Segment.SIZE
     */
    private static final int SEGMENT_SIZE = 8192;

    private static final int MIN_SD_SPACE = 50 * 1024 * 1024;

    /**
     * 章节文件有效大小最小10byte
     */
    private static final int MIN_CHAPTER_FILE_SIZE = 10;

    private static final String NO_SPACE_LEFT_ON_DEVICE = "No space left on device";

    private static final String READ_ONLY_FILE_SYSTEM = "Read-only file system";

    private OkHttpClient mClient;

    LoaderForDz(Context context, OkHttpClient client) {
        super(context);
        mClient = client;
    }

    /**
     * 下载cdn文件
     *
     * @param bookInfo    bookInfo
     * @param catalogInfo catalogInfo
     * @param url         url
     * @param backUrls    backUrls
     * @return result
     */
    public LoadResult loadCdnFile(BookInfo bookInfo, CatalogInfo catalogInfo, String url, List<String> backUrls) {

        addloadLog("内容来源于服务器，cdn地址开始下载，下载url：" + url);

        HwRequestLib.flog("download start chapterId：" + catalogInfo.catalogid);

        if (!URLUtil.isNetworkUrl(url)) {
            dzLoadDesLog(catalogInfo.catalogid, "不是合法的下载url");

            addDownloadFailDzLog(catalogInfo, url, "", "-1", "-1", "-1", "不是合法的下载url");
            return new LoadResult(LoadResult.STATUS_ERROR_URL);
        }

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dzLoadDesLog(catalogInfo.catalogid, "存储卡不可用 not Environment.MEDIA_MOUNTED ");

            addDownloadFailDzLog(catalogInfo, url, "", "-1", "-1", "-1", "存储卡不可用 not Environment.MEDIA_MOUNTED ");
            return new LoadResult(LoadResult.STATUS_ERROR_SDCARD);
        }

        if (catalogInfo.isFileCanUse()) {

            HwRequestLib.flog("download success chapterId：" + catalogInfo.catalogid + "，当前文件可用，直接返回成功结果");

            addloadLog("当前文件可用，直接返回成功结果");
            return new LoadResult(LoadResult.STATUS_SUCCESS);
        }

        File bookDir = new File(BOOK_DIR_PATH + bookInfo.bookid);
        if (!bookDir.exists()) {
            boolean mkdirs = bookDir.mkdirs();
        }
        String kfFileName = catalogInfo.catalogid + ".kf";
        LoadFileBean loadFileBean = downloadFile(bookDir, kfFileName, url, catalogInfo);
        File saveFile = loadFileBean.saveFile;
        //cdn重试
        if (saveFile == null || !saveFile.exists()) {
            if (backUrls != null && backUrls.size() > 0) {
                for (String backUrl : backUrls) {
                    loadFileBean = downloadFile(bookDir, kfFileName, backUrl, catalogInfo);
                    saveFile = loadFileBean.saveFile;
                    if (saveFile != null && saveFile.exists()) {
                        break;
                    }
                }
            }
        }

        LoadResult loadResult = notifyDownloadError(catalogInfo, url, backUrls, loadFileBean, saveFile);
        if (loadResult != null) {
            return loadResult;
        }

        long chapterFileSize = saveFile.length();
        //跟服务器商量 cdn下载文件尺寸大于0，则放过，但是回传911错误，以便方便排查
        if (chapterFileSize > 0) {
            return notifyDownloadSuccess(bookInfo, catalogInfo, url, loadFileBean, saveFile, chapterFileSize);
        } else {
            return notifyDownloadEmpty(catalogInfo, url, loadFileBean, chapterFileSize);
        }
    }

    private LoadResult notifyDownloadEmpty(CatalogInfo catalogInfo, String url, LoadFileBean loadFileBean, long chapterFileSize) {
        addDownloadFailDzLog(catalogInfo, url, "", loadFileBean.responseCode + "", loadFileBean.netFileSize + "", loadFileBean.downloadFileSize + "", "章节内容为空");

        CatalogInfo catalog = new CatalogInfo(catalogInfo.bookid, catalogInfo.catalogid);
        catalog.isdownload = "1";
        catalog.catalogfrom = "0";
        DBUtils.updateCatalog(mContext, catalog);

        String desc = "章节内容为空!-----" + "下载文件大小:" + chapterFileSize + ",下载书籍:自有";
        HwRequestLib.flog("download fail chapterId：" + catalogInfo.catalogid + "," + desc);

        if (!TextUtils.isEmpty(loadFileBean.message)) {
            return new LoadResult(LoadResult.STATUS_DOWNLOAD_FILE_FAIL, loadFileBean.message);
        } else {
            return new LoadResult(LoadResult.STATUS_ERROR_CHAPTER);
        }
    }

    private LoadResult notifyDownloadSuccess(BookInfo bookInfo, CatalogInfo catalogInfo, String url, LoadFileBean loadFileBean, File saveFile, long chapterFileSize) {
        if (chapterFileSize <= MIN_CHAPTER_FILE_SIZE) {
            //服务器下载错误回传
            addDownloadFailDzLog(catalogInfo, url, "", loadFileBean.responseCode + "", loadFileBean.netFileSize + "", loadFileBean.downloadFileSize + "", "下载文件内容小于10字节 (不影响使用，cdn下载放过)");
        }

        CatalogInfo bean = new CatalogInfo(bookInfo.bookid, catalogInfo.catalogid);
        bean.path = saveFile.getAbsolutePath();
        bean.isdownload = "0";
        CatalogInfo cif = DBUtils.getCatalog(mContext, catalogInfo.bookid, catalogInfo.catalogid);
        if (null != cif && TextUtils.isEmpty(cif.dlTime)) {
            bean.dlTime = UtilTimeOffset.getDateFormatSev();
        }
        DBUtils.updateCatalog(mContext, bean);

        HwRequestLib.flog("download success chapterId：" + catalogInfo.catalogid + "，下载成功");

        return new LoadResult(LoadResult.STATUS_SUCCESS);
    }

    @Nullable
    private LoadResult notifyDownloadError(CatalogInfo catalogInfo, String url, List<String> backUrls, LoadFileBean loadFileBean, File saveFile) {
        if (saveFile == null || !saveFile.exists()) {
            //cdn重试后 还是下载失败，打点上传
            if (backUrls != null && backUrls.size() > 0) {
                retryDownloadFailDzLog(catalogInfo, url, backUrls);
            }

            if (!TextUtils.isEmpty(loadFileBean.message)) {
                return new LoadResult(LoadResult.STATUS_DOWNLOAD_FILE_FAIL, loadFileBean.message);
            } else {
                return new LoadResult(LoadResult.STATUS_ERROR);
            }
        }
        return null;
    }


    private void dzLoadDesLog(String catalogId, String desc) {
        addloadLog(desc);
        HwRequestLib.flog("download fail chapterId:" + catalogId + "，" + desc);
    }

    private LoadFileBean downloadFile(File saveDir, String fileName, String url, CatalogInfo catalogInfo) {

        LoadFileBean loadBean = downloadFileInner(saveDir, fileName, url, catalogInfo);

        //重试一遍
        if (loadBean.responseCode == -1) {
            SystemClock.sleep(100);
            loadBean = downloadFileInner(saveDir, fileName, url, catalogInfo);
        }

        return loadBean;
    }

    private LoadFileBean downloadFileInner(File saveDir, String fileName, String url, CatalogInfo catalogInfo) {

        LoadFileBean loadBean = new LoadFileBean();

        BufferedSource source = null;
        BufferedSink sink = null;

        int respCode;

        try {
            Request request = new Request.Builder().url(url).build();
            Response response = mClient.newCall(request).execute();
            respCode = response.code();
            loadBean.responseCode = respCode;

            if (response.isSuccessful()) {
                ResponseBody body = response.body();

                loadBean.netFileSize = body.contentLength();

                source = body.source();
                source.request(Long.MAX_VALUE);

                String result = source.buffer().clone().readUtf8();
                if (StringUtil.isHtml(result) && !NetworkUtils.getInstance().isNetWorkOnline()) {
                    return loadBean;
                }

                ModelAction.readyEnoughSpace(mContext, MIN_SD_SPACE);
                File tempFile = new File(saveDir, getFileName());
                try {
                    readyNewFile(tempFile);
                } catch (Exception e) {
                    ALog.printStack(e);
                    //出现异常 则判断.ishugui文件夹是否为空 如果为空 删除后重新建立
                    checkWorkDir(saveDir, tempFile);
                    readyNewFile(tempFile);
                }

                sink = Okio.buffer(Okio.sink(tempFile));

                Buffer sinkBuffer = sink.buffer();

                long totalRead = 0;
                for (long readCount; (readCount = source.read(sinkBuffer, SEGMENT_SIZE)) != -1; ) {
                    totalRead += readCount;
                    addloadLog("url:" + url + ",下载进度progress:" + totalRead);
                }
                sink.flush();

                File saveFile = tempFileRename(saveDir, fileName, tempFile);

                loadBean.downloadFileSize = saveFile.length();
                loadBean.saveFile = saveFile;
            } else {
                addDownloadFailDzLog(catalogInfo, url, "", respCode + "", "-1", "-1", "服务器响应码错误");
            }

        } catch (Exception e) {
            ALog.printStack(e);
            String emsg = android.util.Log.getStackTraceString(e);
            addloadLog("load file error:" + url + ",msg:" + emsg);

            if (!TextUtils.isEmpty(e.toString())) {
                String exceptionMsg = e.toString();
                if (exceptionMsg.contains(NO_SPACE_LEFT_ON_DEVICE)) {
                    loadBean.message = mContext.getResources().getString(R.string.store_not_enough);
                } else if (exceptionMsg.contains(READ_ONLY_FILE_SYSTEM)) {
                    loadBean.message = mContext.getResources().getString(R.string.sdcard_unavailable);
                }
            }

            addDownloadFailDzLog(catalogInfo, url, e, loadBean.responseCode + "", loadBean.netFileSize + "", loadBean.downloadFileSize + "", "下载文件过程出现异常");
        } finally {
            try {
                if (sink != null) {
                    sink.close();
                }
                if (source != null) {
                    source.close();
                }
            } catch (IOException e) {
                ALog.printStack(e);
            }
        }

        return loadBean;
    }

    private void readyNewFile(File tempFile) throws IOException {
        if (tempFile.getParentFile() != null && !tempFile.getParentFile().exists()) {
            if (!tempFile.mkdirs()) {
                ALog.dWz("mkdirs fail " + tempFile);
            }
        }

        if (tempFile.exists()) {
            if (!tempFile.delete()) {
                ALog.dWz("delete fail " + tempFile);
            }
        }
        if (!tempFile.createNewFile()) {
            ALog.dWz("createNewFile fail " + tempFile);
        }
    }

    private void checkWorkDir(File saveDir, File tempFile) {
        File myFile = new File(mAppRootPath);
        if (myFile.exists()) {
            if (myFile.isDirectory()) {
                String[] tempList = myFile.list();
                if (tempList == null || tempList.length <= 0) {
                    //直接删除目录 后新建
                    FileUtils.delete(myFile);
                    if (saveDir != null && !saveDir.exists()) {
                        if (!saveDir.mkdirs()) {
                            ALog.dWz("mkdirs fail " + tempFile);
                        }
                    }
                }
            } else {
                //直接删除目录 后新建
                FileUtils.delete(myFile);
                if (saveDir != null && !saveDir.exists()) {
                    if (!saveDir.mkdirs()) {
                        ALog.dWz("create fail " + saveDir);
                    }
                }
            }
        }
    }

    private File tempFileRename(File saveDir, String fileName, File tempFile) {
        File saveFile = new File(saveDir, fileName);
        if (saveFile.exists()) {
            if (!saveFile.delete()) {
                ALog.dWz("delete fail " + saveFile);
            }
            saveFile = new File(saveDir, fileName);
            if (!tempFile.renameTo(saveFile)) {
                ALog.dWz("renameTo fail " + saveFile);
            }
        } else if (!tempFile.renameTo(saveFile)) {
            ALog.dWz("renameTo fail " + saveFile);
        }
        return saveFile;
    }

    /**
     * 获取文件名
     */
    private String getFileName() {
        return System.currentTimeMillis() + ".tmp";
    }

    /**
     * 发起自有订购充值，已获取章节内容信息
     *
     * @param activity       activity
     * @param bookInfo       bookInfo
     * @param catalogInfo    catalogInfo
     * @param descFrom       descFrom
     * @param rechargeParams rechargeParams
     * @return result
     */
    public LoadResult dzRechargePay(final Activity activity, BookInfo bookInfo, final CatalogInfo catalogInfo, final String descFrom, final RechargeParams rechargeParams) {
        final HashMap<String, String> payData = getRechargePayMap(activity, descFrom, null, null);
        if (payData == null) {
            return new LoadResult(LoadResult.STATUS_ERROR);
        }

        if (!NetworkUtils.getInstance().checkNet()) {
            return new LoadResult(LoadResult.STATUS_NET_WORK_NOT_USE);
        }

        try {
            payData.put(RechargeMsgResult.BOOK_ID, catalogInfo.bookid);
            payData.put(RechargeMsgResult.CHAPTER_BASE_ID, catalogInfo.catalogid);

            //是否直接购买：1，否；2，是
            payData.put(RechargeMsgResult.AUTO_PAY, bookInfo.payRemind + "");
            //1：非确认订购扣费 2：确认订购-扣费
            payData.put(RechargeMsgResult.CONFIRM_PAY, "1");

            if (rechargeParams != null) {
                payData.put(RechargeMsgResult.READ_ACTION, rechargeParams.getReadAction());

                if (!TextUtils.isEmpty(rechargeParams.getOperateFrom())) {
                    payData.put(RechargeMsgResult.OPERATE_FROM, rechargeParams.getOperateFrom());
                    payData.put(RechargeMsgResult.PART_FROM, rechargeParams.getPartFrom());
                }
            }
            payData.put(RechargeMsgResult.ORDER_STATE, String.valueOf(bookInfo.getLimitConfirmStatus()));

            UtilRecharge manager = UtilRecharge.getDefault();

            final CountDownLatch latch = new CountDownLatch(1);
            final Map<String, String> map = new HashMap<>(16);

            RechargeObserver observer = new RechargeObserver(activity, new Listener() {

                @Override
                public void onSuccess(int ordinal, HashMap<String, String> parm) {
                    ALog.eLwx("pay onSuccess---------------");
                    map.put(RESULT_KEY, String.valueOf(ordinal));
                    map.putAll(parm);
                    latch.countDown();
                }

                @Override
                public void onFail(HashMap<String, String> parm) {
                    ALog.eLwx("pay onFail---------------");
                    map.putAll(parm);
                    latch.countDown();
                }
            }, RechargeAction.PAY_CHECK);

            manager.execute(activity, payData, RechargeAction.PAY_CHECK.ordinal(), observer);

            ALog.eLwx("pay wait---------------");

            if (!latch.await(30, TimeUnit.MINUTES)) {
                ALog.dLk("dzRechargePay latch.await fails");
            }
            if (map.containsKey(RESULT_KEY)) {
                map.remove(RESULT_KEY);

                LoadResult loadResult = new LoadResult(LoadResult.STATUS_SUCCESS);
                loadResult.json = map.get(RechargeMsgResult.REQUEST_JSON);

                boolean isAddBook = TextUtils.equals("2", map.get(RechargeMsgResult.IS_ADD_SHELF));
                if (isAddBook) {
                    MarketDao.markConfirmOnSuccess(mContext, bookInfo, false, 0, rechargeParams.isReader);
                }
                return loadResult;
            }
            ALog.eLwx("pay map no key---------------");

            LoadResult loadResult = new LoadResult(LoadResult.STATUS_RETURN_SDK_ERROR, RechargeMsgUtils.getRechargeMsg(map));
            if (RechargeMsgUtils.isBackCancel(map)) {
                loadResult = new LoadResult(LoadResult.STATUS_CANCEL, RechargeMsgUtils.getRechargeMsg(map));
            }

            if (RechargeMsgUtils.isNeedHwLogin(map)) {
                loadResult.isNeedLogin = true;
            }

            return loadResult;
        } catch (Exception e) {
            ALog.eLwx("pay Exception---------------" + e.getMessage());
            return new LoadResult(LoadResult.STATUS_ERROR);
        }
    }

    /**
     * 获取充值参数map
     *
     * @param ctx             ctx
     * @param descFrom        descFrom
     * @param rechargeMoneyId rechargeMoneyId
     * @param rechargeWay     rechargeWay
     * @return map
     */
    public HashMap<String, String> getRechargePayMap(Context ctx, String descFrom, String rechargeMoneyId, String rechargeWay) {
        if (null == ctx) {
            return null;
        }
        Context context = ctx.getApplicationContext();
        HashMap<String, String> payData = new HashMap<String, String>();

        payData.put(RechargeMsgResult.DESC_FROM, descFrom);
        // +++与cookies上传相关的地址及参数
        payData.put(RechargeMsgResult.SERVICE_URL, RequestCall.getUrlBasic());

        payData.put(RechargeMsgResult.USER_ID, SpUtil.getinstance(context).getUserID());

        if (!TextUtils.isEmpty(rechargeWay)) {
            payData.put(RechargeMsgResult.RECHARGE_WAY, rechargeWay);
        }
        if (!TextUtils.isEmpty(rechargeMoneyId)) {
            payData.put(RechargeMsgResult.RECHARGE_MONEY_ID, rechargeMoneyId);
        }


        HwRequest hwRequest = HwRequestLib.getInstance().getmRequest();
        payData.put(RechargeMsgResult.APP_ID, hwRequest.getAppId());
        payData.put(RechargeMsgResult.COUNTRY, hwRequest.getCOUNTRY());
        payData.put(RechargeMsgResult.LANG, hwRequest.getLang());
        payData.put(RechargeMsgResult.VER, hwRequest.getVer());
        payData.put(RechargeMsgResult.APP_VER, hwRequest.getAppVer());
        payData.put(RechargeMsgResult.APP_TOKEN, SpUtil.getinstance(context).getAppToken());
        payData.put(RechargeMsgResult.P_NAME, mContext.getPackageName());
        payData.put(RechargeMsgResult.CHANNEL_CODE, PackageControlUtils.getChannel());
        payData.put(RechargeMsgResult.UTD_ID, DeviceInfoUtils.getInstanse().getHwUtdId());

        return payData;
    }

}
