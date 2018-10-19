package com.dzbook.log;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.dzbook.bean.QueueBean;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.QueueWorker;

import org.json.JSONObject;

import java.util.HashMap;

import static com.dzbook.log.LogConstants.GH_PI;
import static com.dzbook.log.LogConstants.GH_PN;
import static com.dzbook.log.LogConstants.GH_PS;
import static com.dzbook.log.LogConstants.GH_TYPE;
import static com.dzbook.log.LogConstants.GH_WEB;

/**
 * 拼接额外map参数
 *
 * @author caimantang on 2017/9/5.
 */
public class DzLogMap {
    /**
     * 用于打点
     *
     * @param context  context
     * @param map      map
     * @param bookInfo bookInfo
     * @param catalog  catalog
     * @return HashMap
     */
    public static HashMap<String, String> getReaderMap(Context context, HashMap<String, String> map, BookInfo bookInfo, CatalogInfo catalog) {
        if (null == map) {
            return null;
        }
        if (null != catalog) {
            map.put("ispay", catalog.ispay);
            int catalogNumb = DBUtils.getCatalogNumb(context, catalog.bookid, catalog.id);
            ALog.cmtDebug("catalogNumb:" + catalogNumb);
            map.put("cid_numb", catalogNumb + "");
        }
        if (null != bookInfo) {
            map = addReaderFrom(map, bookInfo.readerFrom);
            //文件类型（1.epub 2.txt）
            map.put("bft", bookInfo.format + "");
            if (2 == bookInfo.bookfrom) {
                //本地
                map.put("src", "1");
            } else if (1 == bookInfo.bookfrom) {
                if (2 == bookInfo.isdefautbook) {
                    //书架默认
                    map.put("src", "2");
                } else if (1 == bookInfo.isdefautbook) {
                    //网络
                    map.put("src", "3");
                }
            }
        }
        return map;
    }

    /**
     * 打点数据
     *
     * @param context context
     * @param map     map
     * @param bookId  bookId
     * @return HashMap
     */
    public static HashMap<String, String> getReaderFrom(Context context, HashMap<String, String> map, String bookId) {
        if (null == map) {
            map = new HashMap<>();
        }
        if (null == context) {
            return map;
        }
        if (!TextUtils.isEmpty(bookId)) {
            BookInfo bookInfo = DBUtils.findByBookId(context, bookId);
            if (null != bookInfo) {
                map = addReaderFrom(map, bookInfo.readerFrom);
            }
        }
        return map;
    }

    /**
     * addReaderFrom
     *
     * @param map  map
     * @param from from
     * @return HashMap
     */
    public static HashMap<String, String> addReaderFrom(HashMap<String, String> map, String from) {
        if (null == map) {
            map = new HashMap<>();
        }
        if (!TextUtils.isEmpty(from)) {
            try {
                String ghPi = "";
                String ghPn = "";
                String ghPs = "";
                String ghType = "";
                String ghWeb = "";
                JSONObject jsonObject = new JSONObject(from);
                if (jsonObject.has(GH_PI)) {
                    ghPi = jsonObject.optString(GH_PI);
                }
                if (jsonObject.has(GH_PN)) {
                    ghPn = jsonObject.optString(GH_PN);
                }
                if (jsonObject.has(GH_PS)) {
                    ghPs = jsonObject.optString(GH_PS);
                }
                if (jsonObject.has(GH_TYPE)) {
                    ghType = jsonObject.optString(GH_TYPE);
                }
                if (!TextUtils.isEmpty(ghPi)) {
                    map.put(GH_PI, ghPi);
                }
                if (!TextUtils.isEmpty(ghPs)) {
                    map.put(GH_PS, ghPs);
                }
                if (!TextUtils.isEmpty(ghPn)) {
                    map.put(GH_PN, ghPn);
                }
                if (!TextUtils.isEmpty(ghType)) {
                    map.put(GH_TYPE, ghType);
                }
                if (!TextUtils.isEmpty(ghWeb)) {
                    map.put(GH_WEB, ghWeb);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * getPreLastMap
     *
     * @return HashMap
     */
    public static HashMap<String, String> getPreLastMap() {
        QueueBean queueBean = QueueWorker.getInstance().getFromEnd();
        if (null == queueBean) {
            return null;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LogConstants.MAP_PN, queueBean.name);
        if (null != queueBean.map) {
            String pi = queueBean.map.get(LogConstants.MAP_PI);
            map.put(LogConstants.MAP_PI, !TextUtils.isEmpty(pi) ? pi : "");
            String ps = queueBean.map.get(LogConstants.MAP_PS);
            map.put(LogConstants.MAP_PS, !TextUtils.isEmpty(ps) ? ps : "");
        }
        return map;
    }

    /**
     * 解析url
     *
     * @param url url
     * @return url
     */
    public static String parseUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        Uri parse = Uri.parse(url);
        if (null == parse) {
            return "";
        }
        return parse.getScheme() + "://" + parse.getAuthority() + parse.getPath();
    }
}
