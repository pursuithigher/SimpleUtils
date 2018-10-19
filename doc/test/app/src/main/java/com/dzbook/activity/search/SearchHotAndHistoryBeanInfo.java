package com.dzbook.activity.search;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.SpUtil;

import java.util.LinkedList;
import java.util.List;

import hw.sdk.net.bean.seach.BeanKeywordHotVo;
import hw.sdk.net.bean.seach.BeanSearchHot;

/**
 * 搜索热词+历史 Bean
 *
 * @author dongdianzhou on 2017/3/30.
 */
public class SearchHotAndHistoryBeanInfo {

    /**
     * 搜索热点词
     */
    public BeanSearchHot searchHotInfo;

    private SearchHistoryBeanInfo searchHistoryInfo;

    private boolean isInitHistoryList = false;

    /**
     * 构造
     */
    public SearchHotAndHistoryBeanInfo() {
        searchHistoryInfo = new SearchHistoryBeanInfo();
        searchHotInfo = new BeanSearchHot();
    }

    /***
     * 搜索页面热门是否存在数据
     * 1. 编辑框热词
     * 2. 热门搜索热词
     * 3. 搜索历史列表
     * @return boolean
     */
    public boolean isExistData() {
        if (searchHotInfo != null && searchHotInfo.isExistData()) {
            return true;
        }
        return isExistHistoryList();
    }

    public BeanSearchHot getSearchHotInfo() {
        return searchHotInfo;
    }

    /**
     * 进入搜索页面调用初始化historylist
     *
     * @param context context
     */
    public void initHotHistory(Context context) {
        isInitHistoryList = true;
        if (searchHistoryInfo != null) {
            searchHistoryInfo.getSearchList(context);
        }
    }

    /**
     * 获取搜索列表
     *
     * @return list
     */
    public List<String> getHistoryList() {
        if (!isInitHistoryList) {
            throw new RuntimeException("获取搜索历史前需要初始化搜索历史");
        }
        if (searchHistoryInfo != null) {
            return searchHistoryInfo.historyList;
        }
        return null;
    }

    /**
     * 清除搜索列表
     */
    public void clearHistoryList() {
        if (searchHistoryInfo != null) {
            searchHistoryInfo.clearHistoryList();
        }
    }

    /**
     * 添加搜索列表
     *
     * @param key key
     */
    public void addHistoryList(String key) {
        if (searchHistoryInfo != null) {
            searchHistoryInfo.addSearchHistory(key);
        }
    }

    /**
     * 保存搜索数据
     *
     * @param context context
     */
    public void saveSearchHistoryToShareFile(Context context) {
        if (searchHistoryInfo != null) {
            searchHistoryInfo.saveSearchListToShare(context);
        }
    }

    /**
     * 是否存在搜索历史
     *
     * @return boolean
     */
    public boolean isExistHistoryList() {
        return searchHistoryInfo != null && searchHistoryInfo.isExistHistoryList();
    }

    /**
     * 是否存在搜索编辑框的热词
     *
     * @return boolean
     */
    public boolean isExistSearchEditKey() {
        return searchHotInfo != null && searchHotInfo.isExistSearchEditKey();
    }

    /**
     * 是否存在热门搜索词列表
     *
     * @return boolean
     */
    public boolean isExistSearchHotKeys() {
        return searchHotInfo != null && searchHotInfo.isExistSearchHotKeys();
    }

    /**
     * 获取编辑框的热词列表
     *
     * @return list
     */
    public List<String> getSearchEditKeys() {
        if (searchHotInfo != null) {
            return searchHotInfo.keywordDefault;
        }
        return null;
    }

    /**
     * 获取热门搜索热词列表
     *
     * @return list
     */
    public List<BeanKeywordHotVo> getSearchHotKeys() {
        if (searchHotInfo != null) {
            return searchHotInfo.keywordHot;
        }
        return null;
    }

    /**
     * 搜索历史
     */
    public static class SearchHistoryBeanInfo {

        private static final int HISTORY_NUM_MAX = 5;
        /**
         * 搜索历史列表
         */
        public LinkedList<String> historyList;

        /**
         * 构造
         */
        public SearchHistoryBeanInfo() {
            historyList = new LinkedList<String>();
        }

        public boolean isExistHistoryList() {
            return historyList != null && historyList.size() > 0;
        }

        /**
         * 将搜索历史存储在内存中，不需要反复的读取share文件
         * 搜索历史限制HISTORY_NUM_MAX条
         * 超过5条自动移除最后一条，添加最新的一条
         *
         * @param key key
         */
        public void addSearchHistory(String key) {
            //            ALog.eDongdz("addSearchHistory:key:" + key);
            if (historyList != null) {
                if (historyList.size() > 0 && historyList.contains(key)) {
                    historyList.remove(key);
                }
                if (historyList.size() < HISTORY_NUM_MAX) {
                    historyList.addFirst(key);
                } else {
                    historyList.removeLast();
                    historyList.addFirst(key);
                }
            }
        }

        /**
         * 将搜索记录存储到share文件，只有在搜索页面finsh掉的时候调用
         * 存储格式：key,key,key,
         *
         * @param context context
         */
        public void saveSearchListToShare(Context context) {
            if (historyList != null) {
                StringBuffer keysBuffer = new StringBuffer();
                for (String keyStr : historyList) {
                    if (!TextUtils.isEmpty(keyStr)) {
                        keysBuffer = keysBuffer.append(keyStr + ",");
                    }
                }
                String keysStr = keysBuffer.toString();
                if (!TextUtils.isEmpty(keysStr) && keysStr.length() > 1) {
                    keysStr = keysStr.substring(0, keysStr.length() - 1);
                }
                //                ALog.eDongdz("搜索历史：" + keys);
                //华为要求对搜索数据进行加密，属于用户隐私
                try {
                    if (!TextUtils.isEmpty(keysStr)) {
                        String encrypt = Base64.encodeToString(keysStr.getBytes("UTF-8"), Base64.DEFAULT);
                        if (!TextUtils.isEmpty(encrypt)) {
                            ALog.cmtDebug("keysStr:" + keysStr);
                            ALog.cmtDebug("encrypt:" + encrypt);
                            SpUtil.getinstance(context).setKeySearchHistory(encrypt);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        /**
         * 清除内存中的搜索列表，页面需刷新
         */
        public void clearHistoryList() {
            if (historyList != null && historyList.size() > 0) {
                historyList.clear();
                SpUtil.getinstance(AppConst.getApp()).setKeySearchHistory("");
            }
        }

        /**
         * 获取搜索历史列表
         *
         * @param context context
         * @return list
         */
        public List<String> getSearchList(Context context) {
            try {
                String keys = SpUtil.getinstance(context).getKeySearchHistory();
                byte[] decode = Base64.decode(keys, Base64.DEFAULT);
                String decrypt = new String(decode, "UTF-8");
                if (!TextUtils.isEmpty(decrypt)) {
                    ALog.cmtDebug("keys:" + keys);
                    ALog.cmtDebug("decrypt:" + decrypt);
                    String[] keyNums = decrypt.split(",");
                    if (keyNums != null && keyNums.length > 0) {
                        for (String key : keyNums) {
                            historyList.add(key);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return historyList;
        }
    }
}
