package com.dzbook.loader;

import android.content.Context;
import android.text.TextUtils;

import com.dzbook.database.bean.CatalogInfo;
import com.ishugui.R;

import java.util.List;

/**
 * LoadResult
 *
 * @author wxliao on 17/8/2.
 */

public class LoadResult {
    /**
     * 状态-SUCCESS
     */
    public static final int STATUS_SUCCESS = 0x01;

    /**
     * 状态_ERROR
     */
    public static final int STATUS_ERROR = 0x11;

    /**
     * 状态_ERROR_CHAPTER
     */
    public static final int STATUS_ERROR_CHAPTER = 0x12;

    /**
     * 状态_ERROR_URL
     */
    public static final int STATUS_ERROR_URL = 0x13;

    /**
     * 状态_ERROR_SDCARD
     */
    public static final int STATUS_ERROR_SDCARD = 0x14;

    /**
     * 状态_ERROR_113
     */
    public static final int STATUS_ERROR_113 = 0x15;

    /**
     * 状态_ERROR_BOOK_OFF
     */
    public static final int STATUS_ERROR_BOOK_OFF = 0x16;

    /**
     * 状态_ERROR_BULK_DISABLE
     */
    public static final int STATUS_ERROR_BULK_DISABLE = 0x17;

    /**
     * 状态_ERROR_236
     */
    public static final int STATUS_ERROR_236 = 0x18;

    /**
     * 状态_NET_WORK_NOT_COOL
     */
    public static final int STATUS_NET_WORK_NOT_COOL = 0x19;

    /**
     * 状态_NET_WORK_NOT_USE
     */
    public static final int STATUS_NET_WORK_NOT_USE = 0x20;

    /**
     * 状态_CANCEL
     */
    public static final int STATUS_CANCEL = 0x21;

    /**
     * 为了返回sdk内部错误，都以sdk内容为准 例如：取消订购等sdk不返回错误信息
     */
    public static final int STATUS_RETURN_SDK_ERROR = 0x22;

    /**
     * 最后一个章节
     */
    public static final int STATUS_ALREADY_LAST_CHAPTER = 0x23;

    /**
     * 下载文件失败 返回内部错误
     */
    public static final int STATUS_DOWNLOAD_FILE_FAIL = 0x24;

    /**
     * VIP书籍不支持批量下载
     */
    public static final int STATUS_ERROR_BULK_DISABLE_FOR_VIP = 0x25;

    /**
     * 状态码
     */
    public int status;

    /**
     * 章节信息
     */
    public CatalogInfo mChapter;


    /**
     * 信息
     */
    public String message;

    /**
     * json
     */
    public String json;

    /**
     * 给阅读器目录下载已购章节的下载集合
     */
    public List<String> idList;

    /**
     * 是否需要登录
     * 主要为了订购情况下返回登录，客户端需要登录后再次发起订购请求
     */
    public boolean isNeedLogin;


    /**
     * 构造函数
     *
     * @param status status
     */
    public LoadResult(int status) {
        this.status = status;
    }

    /**
     * 构造函数
     *
     * @param status  status
     * @param message message
     */
    public LoadResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * 给阅读器目录批量下载已购章节使用
     *
     * @param status status
     * @param idList idList
     */
    public LoadResult(int status, List<String> idList) {
        this.status = status;
        this.idList = idList;
    }

    /**
     * 是否返回取消
     * 主要是为了判断
     * 单章订购时，点击批量订购，批量订购点击返回返回单章订购
     *
     * @return boolean
     */
    public boolean isCanceled() {
        return STATUS_CANCEL == status;
    }

    /**
     * 是否成功
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return status == STATUS_SUCCESS;
    }

    public boolean isChapterError() {
        return status == STATUS_ERROR_CHAPTER;
    }


    /**
     * 获取消息
     *
     * @param context context
     * @return string
     */
    public String getMessage(Context context) {

        if (!TextUtils.isEmpty(message)) {
            return message;
        }
        if (status == STATUS_ERROR_113) {
            return context.getString(R.string.preload_load_fail);
        } else if (status == STATUS_NET_WORK_NOT_COOL) {
            return context.getString(R.string.net_work_notuse);
        } else if (status == STATUS_NET_WORK_NOT_USE) {
            return context.getString(R.string.net_work_notuse);
        } else if (status == STATUS_ERROR_BOOK_OFF) {
            return context.getString(R.string.book_down_shelf);
        } else if (status == STATUS_ERROR_CHAPTER) {
            return context.getString(R.string.download_chapter_error);
        } else if (status == STATUS_ERROR_SDCARD) {
            return context.getString(R.string.preload_sdcard_notexist);
        } else if (status == STATUS_ERROR_URL) {
            return context.getString(R.string.download_chapter_error);
        } else if (status == STATUS_ERROR_236) {
            return context.getString(R.string.preload_load_fail);
        } else if (status == STATUS_ERROR_BULK_DISABLE) {
            return context.getString(R.string.free_book_not_support_downlod);
        } else if (status == STATUS_ERROR_BULK_DISABLE_FOR_VIP) {
            return context.getString(R.string.vip_book_not_support_downlod);
        } else if (status == STATUS_ERROR) {
            /*if (!NetworkUtils.getInstance().checkNet()) {
                return context.getString(R.string.open_book_fail);
            } else {*/
            return context.getString(R.string.preload_load_fail);
            //}

        }
        return "";
    }

    public boolean isNetError() {
        return status == LoadResult.STATUS_NET_WORK_NOT_USE || status == LoadResult.STATUS_NET_WORK_NOT_COOL || status == LoadResult.STATUS_ERROR;
    }


}
