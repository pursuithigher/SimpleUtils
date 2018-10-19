package com.dzbook.loader;

import android.content.Context;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.ListUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import hw.sdk.net.bean.BeanChapterCatalog;
import hw.sdk.net.bean.BeanChapterInfo;

/**
 * 内容加载
 *
 * @author wxliao on 17/8/2.
 */
public class BaseLoader {

    /**
     * 书籍路径
     */
    public static final String BOOK_DIR_PATH = SDCardUtil.getInstance().getSDCardAndroidRootDir() + File.separator + FileUtils.APP_BOOK_DIR_PATH;
    /**
     * RESULT_KEY
     */
    public static final String RESULT_KEY = "ordinal";

    private static final String LOAD_TAG = "load_tag";
    /**
     * app根路径
     */
    public final String mAppRootPath = SDCardUtil.getInstance().getSDCardAndroidRootDir() + File.separator + FileUtils.APP_ROOT_DIR_PATH;

    protected Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public BaseLoader(Context context) {
        mContext = context;
    }


    /**
     * 得到当前章节之后所有的章节信息
     *
     * @param bookInfo       bookInfo
     * @param startChapterId startChapterId
     * @param chapterStatus  chapterStatus
     * @return list
     */
    public List<BeanChapterInfo> getChaptersFromServer(BookInfo bookInfo, String startChapterId, String chapterStatus) {
        BeanChapterCatalog beanChapterCatalog = null;
        try {
            String bookId = "";
            if (null != bookInfo) {
                bookId = bookInfo.bookid;
            }
            beanChapterCatalog = HwRequestLib.getInstance().chapterCatalog(bookId, startChapterId, chapterStatus, "", "");
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        if (null != beanChapterCatalog && !ListUtils.isEmpty(beanChapterCatalog.chapterInfoList)) {
            return beanChapterCatalog.chapterInfoList;
        }
        return null;
    }


    void addloadLog(String msg) {
        ALog.d(LOAD_TAG, msg);
    }

    /**
     * 内容下载失败 打点
     *
     * @param mCatalogInfo mCatalogInfo
     * @param cdnUrl       cdnUrl
     * @param exception    exception
     * @param responseCode responseCode
     * @param netSize      netSize
     * @param downloadSize downloadSize
     * @param des          des
     */
    void addDownloadFailDzLog(CatalogInfo mCatalogInfo, String cdnUrl, Object exception, String responseCode, String netSize, String downloadSize, String des) {

        if (mCatalogInfo != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(LogConstants.KEY_BID, mCatalogInfo.bookid);
            map.put(LogConstants.KEY_CID, mCatalogInfo.catalogid);
            map.put(LogConstants.KEY_XZNRSB_URL, cdnUrl);
            map.put(LogConstants.KEY_EXCEPTION, exception);
            map.put(LogConstants.KEY_RESPONSE_CODE, responseCode);
            map.put(LogConstants.KEY_NET_SIZE, netSize);
            map.put(LogConstants.KEY_DLDSIZE, downloadSize);
            map.put(LogConstants.KEY_DES, des);
            DzLog.getInstance().logEventMapObj(LogConstants.EVENT_XZNRSB, map, "");
        }
    }

    /**
     * 内容下载失败 打点
     *
     * @param mCatalogInfo mCatalogInfo
     * @param cdnUrl       cdnUrl
     * @param backUrls     backUrls
     */
    void retryDownloadFailDzLog(CatalogInfo mCatalogInfo, String cdnUrl, List<String> backUrls) {

        if (mCatalogInfo != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(LogConstants.KEY_BID, mCatalogInfo.bookid);
            map.put(LogConstants.KEY_CID, mCatalogInfo.catalogid);
            map.put(LogConstants.KEY_XZNRSB_URL, cdnUrl);
            map.put(LogConstants.KEY_XZNRSB_BACKUP_URL, backUrls);
            DzLog.getInstance().logEventMapObj(LogConstants.EVENT_CSXZNRSB, map, "");
        }
    }
}
