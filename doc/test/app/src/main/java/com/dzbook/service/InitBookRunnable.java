package com.dzbook.service;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hw.sdk.net.bean.BeanChapterInfo;
import hw.sdk.net.bean.BeanSingleBookInfo;

/**
 * 内置书功能。
 *
 * @author zhenglk
 */
public class InitBookRunnable implements Runnable {

    /**
     * IS_BOOK_INIT
     */
    public static final String IS_BOOK_INIT = "is.book.register";

    private Context mContext;

    private int userSelect;

    private long delay;


    InitBookRunnable(Context context, int userSelect, long delay) {
        mContext = context;
        this.userSelect = userSelect;
        this.delay = delay;
    }

    @Override
    public void run() {

        ALog.dLk("内置书籍开始执行");
        ThirdPartyLog.onEventValueOldClick(mContext, ThirdPartyLog.JS_BUILD_SUM, "", 1);

        if (SDCardUtil.getInstance().isSDCardCanWrite()) {
            // 等待设置内置图书类型。
            clientInit();
            SpUtil.getinstance(mContext).setBoolean(IS_BOOK_INIT, true);
            SpUtil.getinstance(mContext).setShelfBookList("1");

            EventBusUtils.sendMessage(EventConstant.SHELF_LOCAL_REFRESH, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
            ALog.dLk("内置书籍完成");
        }
    }

    private void clientInit() {

        long startTime = System.currentTimeMillis();
        String jsonStr;
        while (true) {
            jsonStr = SpUtil.getinstance(mContext).getShelfBookList();
            ALog.dLk("内置书籍开始执行jsonStr:" + jsonStr);
            if ("-1".equals(jsonStr) || ("0".equals(jsonStr) && System.currentTimeMillis() - startTime > delay)) {
                ALog.dLk("内置书 网络数据获取失败，直接内置assert目录内置书籍");
                ThirdPartyLog.onEventValueOldClick(mContext, ThirdPartyLog.JS_BUILD_IN, "-1".equals(jsonStr) ? ThirdPartyLog.NET_FAIL_BUILD_ASSERT : ThirdPartyLog.NET_TIME_OUT_BUILD_ASSERT, System.currentTimeMillis() - startTime);
                initializeData(2, null);
                break;
            } else if ("1".equals(jsonStr)) {
                return;
            } else if (null != jsonStr && jsonStr.length() > 5) {

                ALog.dLk("内置书 网络数据获取成功，直接内置网络数据");

                boolean flag = initializeData(1, jsonStr);
                if (!flag) {
                    ALog.dLk("内置书 网络数据内置失败，转而内置assert目录书籍");
                    ThirdPartyLog.onEventValueOldClick(mContext, ThirdPartyLog.JS_BUILD_IN_APP, ThirdPartyLog.NET_SUC_BUILD_FAIL_BUILD_ASSERT, System.currentTimeMillis() - startTime);
                    initializeData(2, null);
                } else {
                    ThirdPartyLog.onEventValueOldClick(mContext, ThirdPartyLog.JS_BUILD_NET_SUCCESS, "", 1);
                }
                break;
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignore) {
            }
        }

    }


    /**
     * 初始化数据(用的是代码方式初始化默认书籍 )
     *
     * @param dateFrom     1, 服务端下发资源。2，assets 内置资源
     * @param date：内置书json
     */
    private boolean initializeData(int dateFrom, String date) {
        String assetsRoot = "plug_books/";
        JSONObject jsonAllBook;
        AssetManager assetManager = mContext.getAssets();

        try {
            //是否存在基地图书文件夹
            String[] assetsFileList = assetManager.list("plug_books");
            if (1 == dateFrom) {
                //动态内置书籍
                jsonAllBook = new JSONObject(date);
            } else {
                if (assetsFileList == null || assetsFileList.length <= 0) {
                    return false;
                }
                String allBook = readAssets(assetManager, assetsRoot + "book_list.json", "UTF-8");
                if (TextUtils.isEmpty(allBook)) {
                    return false;
                }
                jsonAllBook = new JSONObject(allBook);
            }

            JSONArray jsonArray;
            switch (userSelect) {
                case SpUtil.SELECT_GIRL:
                    jsonArray = jsonAllBook.optJSONArray("girls");
                    break;
                case SpUtil.SELECT_BOY:
                default:
                    jsonArray = jsonAllBook.optJSONArray("boys");
                    break;
            }

            int length = null == jsonArray ? 0 : jsonArray.length();
            if (length <= 0) {
                return false;
            }

            HashSet<String> coverSet = new HashSet<>();
            if (assetsFileList != null && assetsFileList.length > 0) {
                for (String assetsName : assetsFileList) {
                    if (!TextUtils.isEmpty(assetsName) && assetsName.endsWith(".jpg")) {
                        coverSet.add(assetsName);
                    }
                }
            }

            addBooks(jsonArray, coverSet, assetsRoot);
            return true;
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return false;
    }

    private void addBooks(JSONArray jsonArray, HashSet<String> coverSet, String assetsRoot) {
        HwRequestLib.flog("Insert book from " + assetsRoot);

        int length = jsonArray.length();
        //批量插入 效率高
        final List<BookInfo> bookInfoList = new ArrayList<>();
        final List<CatalogInfo> catalogInfoList = new ArrayList<>();

        int count = 0;
        for (int i = length - 1; i >= 0; i--) {
            JSONObject jsonBookDetail = jsonArray.optJSONObject(i);
            if (null == jsonBookDetail) {
                continue;
            }
            BeanSingleBookInfo bean = new BeanSingleBookInfo().parseJSON(jsonBookDetail);
            if (bean.bookInfo != null) {
                String bookId = bean.bookInfo.bookId;
                if (TextUtils.isEmpty(bookId)) {
                    continue;
                }
                BookInfo bookInfoBean = DBUtils.findByBookId(mContext, bookId);
                if (bookInfoBean != null) {
                    continue;
                }
                // ++追加 bookInfo
                bookInfoBean = InsertBookInfoDataUtil.initBookInfo(bean.chapterList,
                        bean.bookInfo, true, true);

                //重置时间，System.currentTimeMillis()有时候会存在bug
                bookInfoBean.time = (System.currentTimeMillis() + count) + "";

                count++;
                // 有内置封面，内置封面缓存
                if (!TextUtils.isEmpty(bookInfoBean.coverurl)) {
                    //内置书封面不再使用自己的内存缓存而是使用glid所以此处不再IO操作，直接赋值给coverurl即可
                    if (coverSet.contains(bookId + ".jpg")) {
                        bookInfoBean.coverurl = "assets:" + assetsRoot + bookId + ".jpg";
                    }
                }
                bookInfoList.add(bookInfoBean);
                // --追加 bookInfo

                // ++追加 catalogInfo
                List<BeanChapterInfo> listChapterInfo = bean.chapterList;
                int size = null == listChapterInfo ? 0 : listChapterInfo.size();
                for (int j = 0; j < size; j++) {
                    BeanChapterInfo netBean = listChapterInfo.get(j);
                    if (null != netBean) {
                        CatalogInfo catalogInfo = InsertBookInfoDataUtil.initCatalogInfo(netBean, bookId);
                        catalogInfoList.add(catalogInfo);
                    }
                }
                // --追加 catalogInfo
            }
        }
        //批量插入
        DBUtils.insertBooks(mContext, bookInfoList);
        DBUtils.insertLotCatalog(mContext, catalogInfoList);
    }


    /**
     * 读取assets文本
     *
     * @param assetManager assetManager
     * @param path         assets path
     * @param charsetName  charsetName
     * @return 文本
     * @throws IOException                  :IO
     * @throws UnsupportedEncodingException if {@code charsetName} is not supported.
     */
    private String readAssets(AssetManager assetManager, String path, String charsetName) throws IOException {
        InputStream is = null;
        try {
            is = assetManager.open(path);
            byte[] bs = new byte[is.available()];
            is.read(bs);
            return new String(bs, charsetName);
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException ignore) {
            }
        }
    }


}
