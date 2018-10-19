package com.dzbook.mvp.presenter;

import android.os.SystemClock;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.AppContext;
import com.dzbook.activity.search.SearchHotAndHistoryBeanInfo;
import com.dzbook.activity.search.SearchKeysBeanInfo;
import com.dzbook.bean.QueueBean;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.SearchUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.QueueWorker;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.WhiteListWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hw.sdk.net.bean.seach.BeanSearch;
import hw.sdk.net.bean.seach.BeanSearchHot;
import hw.sdk.net.bean.seach.BeanSuggest;

/**
 * 搜索
 *
 * @author dongdianzhou on 2017/3/29.
 */

public class SearchPresenterImpl extends BasePresenter {

    long[] mHits = new long[2];
    /**
     * 搜索的关键词类型（用于log）
     * 参考文档
     */
    private String searchKeyType = "0";

    /**
     * 关键词的提示词结果缓存：
     * 作用：内存缓存关键词的提示词结果，避免反复的请求数据库和网络
     * key：关键字
     */
    private Map<String, SearchKeysBeanInfo> keysCacheMap;

    /**
     * 获取到搜索的key
     */
    private String searchKey = "";
    private String searchType = "";
    private SearchKeysBeanInfo searchKeysBeanInfo;
    private SearchHotAndHistoryBeanInfo mSearchHotAndHistory;

    private boolean isSetEditTextKey;
    private int strTotalNum = 15;
    private int strPage = 1;
    private SearchUI mUI;

    /**
     * 初始构造
     *
     * @param searchUI searchUI
     */
    public SearchPresenterImpl(SearchUI searchUI) {
        mUI = searchUI;
        keysCacheMap = new HashMap<String, SearchKeysBeanInfo>();
    }

    private String getSearchKeyType() {
        return searchKeyType;
    }


    /**
     * 获取搜索热词
     */
    public void getHotSearchDataFromNet() {
        isSetEditTextKey = false;
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                //本地缓存
                mUI.showLoadDataDialog();
                mSearchHotAndHistory = new SearchHotAndHistoryBeanInfo();
                //初始化历史数据
                mSearchHotAndHistory.initHotHistory(mUI.getContext());
                //share文件读取热词数据并解析到对象
                HttpCacheInfo cacheInfo = DBUtils.findHttpCacheInfo(mUI.getContext(), RequestCall.SEARCH_HOT_CALL);
                if (cacheInfo != null) {
                    String interfaceData = cacheInfo.response;
                    if (!TextUtils.isEmpty(interfaceData)) {
                        try {
                            mSearchHotAndHistory.getSearchHotInfo().parseJSON(new JSONObject(interfaceData));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mSearchHotAndHistory.isExistData()) {
                        setEditKeysData();
                        mUI.setHotAndHistoryData(mSearchHotAndHistory);
                        mUI.dismissLoadDataDialog();
                    } else {
                        //网络判断
                        if (!NetworkUtils.getInstance().checkNet()) {
                            mUI.showNoNetConnectView(0);
                            return;
                        }
                    }
                }
                //网络请求
                try {
                    final BeanSearchHot beanSearchHot = HwRequestLib.getInstance().searchHotRequest(SpUtil.getinstance(AppConst.getApp()).getPersonReadPref());
                    DzSchedulers.main(new Runnable() {
                        @Override
                        public void run() {
                            if (beanSearchHot != null && beanSearchHot.isSuccess()) {
                                if (beanSearchHot.isExistData()) {
                                    mSearchHotAndHistory.searchHotInfo = beanSearchHot;
                                    setEditKeysData();
                                    mUI.setHotAndHistoryData(mSearchHotAndHistory);
                                } else {
                                    mUI.dismissLoadDataDialog();
                                }
                            } else {
                                //接口响应数据失败
                                mUI.showNoNetConnectView(0);
                            }
                        }
                    });
                } catch (Exception e) {
                    mUI.dismissLoadDataDialog();
                    mUI.showNoNetConnectView(0);
                    setEditKeysData();
                    ALog.printStackTrace(e);
                }
            }
        });
    }

    private void setEditKeysData() {
        if (mSearchHotAndHistory.isExistSearchEditKey() && !isSetEditTextKey) {
            isSetEditTextKey = true;
            List<String> list = mSearchHotAndHistory.getSearchEditKeys();
            if (list != null && list.size() > 0) {
                if (AppContext.getSearchShowIndex() >= list.size()) {
                    AppContext.setSearchShowIndex(0);
                }
                String hotKey = list.get(AppContext.getSearchShowIndex());
                if (!TextUtils.isEmpty(hotKey) && mUI != null) {
                    mUI.setEditTextData(hotKey);
                }
                AppContext.setSearchShowIndex(AppContext.getSearchShowIndex() + 1);
            }
        }
    }

    /**
     * 将搜索记录存储到share文件
     */
    public void saveSearchHistoryToShareFile() {
        if (mSearchHotAndHistory != null) {
            mSearchHotAndHistory.saveSearchHistoryToShareFile(mUI.getContext());
        }
    }

    /**
     * 销毁回调
     */
    public void destroyCallBack() {
        saveSearchHistoryToShareFile();

        if (keysCacheMap != null && keysCacheMap.size() > 0) {
            keysCacheMap.clear();
        }
    }


    /**
     * 获取联想词
     *
     * @param key key
     */
    public void getPromptKeys(String key) {
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.netErrorPage();
            return;
        }
        if (!TextUtils.isEmpty(key) && key.length() > 30) {
            //大于30个字符就不在进行联想词请求
            return;
        }
        //500毫秒内再次联想不请求
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[1] >= (mHits[0] + 500)) {
            getPromptKeysByNet(key);
        }
    }

    /**
     * 添加历史记录
     *
     * @param key key
     */
    public void addHistoryList(String key) {
        if (mSearchHotAndHistory != null) {
            mSearchHotAndHistory.addHistoryList(key);
            mUI.referenceHistory(mSearchHotAndHistory);
        }
    }

    /**
     * 由于时间比较紧和搜索逻辑相对复杂，不再重构这一块，以后重构的时候在处理
     *
     * @param key         key
     * @param isOtherJump isOtherJump
     * @param keyType     keyType
     * @param searchType1 searchType1
     */
    public void searchkey(String key, String keyType, String searchType1, boolean isOtherJump) {
        //TODO 搜索接口及其结果展示待实现
        if (NetworkUtils.getInstance().checkNet()) {
            if ("2".equals(searchType1)) {
                searchKeyType = LogConstants.ZONE_SSYM_ZZSS;
            } else if ("3".equals(searchType1)) {
                searchKeyType = LogConstants.ZONE_SSYM_BQSS;
            } else {
                searchKeyType = keyType;
            }
            addSearchClickLog(key, searchKeyType);
            searchKey = key;
            this.searchType = searchType1;
            strPage = 1;
            if (isOtherJump) {
                search(key, searchType1);
            } else {
                addHistoryList(key);
                if (mSearchHotAndHistory != null) {
                    //        isShowHotDataView = false;//搜索接口调用需要加这个开关，具体看注释
                    search(key, searchType1);
                }
            }
        } else {
            mUI.netErrorPage();
        }
    }

    /**
     * 检测是否有网络，有网不做操作
     */
    public void checkNet() {
        if (!NetworkUtils.getInstance().checkNet()) {
            mUI.netErrorPage();
        }
    }

    private void search(String key, String searchType1) {
        if (!TextUtils.isEmpty(key)) {
            mUI.hideKeyboard();
            strPage = 1;
            if (NetworkUtils.getInstance().checkNet()) {
                mUI.showLoadDataDialog();
                searchBookListTask(true, key, searchType1);

            } else {
                mUI.netErrorPage();
            }
        }

    }

    /**
     * 搜索历史/热门搜索设置edittext的text
     *
     * @param tags        tags
     * @param keyType     keyType
     * @param searchType1 searchType1
     * @param isOtherJump isOtherJump
     */
    public void searchFixedKey(String tags, String keyType, String searchType1, boolean isOtherJump) {
        mUI.setEditTextData(tags, false);
        searchkey(tags, keyType, searchType1, isOtherJump);
    }

    /**
     * 添加标签点击日志
     *
     * @param bookId bookId
     * @param index  index
     */
    public void addTagClickLog(String bookId, int index) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LogConstants.KEY_SEARCHRESULT_BID, bookId);
        map.put(LogConstants.KEY_SEARCHRESULT_INDEX, index + "");
        DzLog.getInstance().logClick(LogConstants.MODULE_SSJGYM, LogConstants.ZONE_SSJGYM_BQPP, searchKey, map, null);
    }

    /**
     * 添加搜索结果日志
     *
     * @param bookId bookId
     * @param index  index
     */
    public void addResultClickLog(String bookId, int index) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(LogConstants.KEY_SEARCHRESULT_BID, bookId);
        map.put(LogConstants.KEY_SEARCHRESULT_INDEX, index + "");
        if (mUI.getSearchResultType()) {
            DzLog.getInstance().logClick(LogConstants.MODULE_SSJGYM, LogConstants.ZONE_SSJGYM_TJSJ, searchKey, map, null);
        } else {
            DzLog.getInstance().logClick(LogConstants.MODULE_SSJGYM, LogConstants.ZONE_SSJGYM_MZPP, searchKey, map, null);
        }
    }

    /**
     * 加载更多
     */
    public void getLoadMorePase() {
        String edsString = getSearchKey();
        String searchType1 = getSearchType();
        if (!TextUtils.isEmpty(edsString)) {
            try {
                strPage = strPage + 1;
            } catch (NumberFormatException e) {
                ALog.printStackTrace(e);
            }
            if (NetworkUtils.getInstance().checkNet()) {
                searchBookListTask(false, edsString, searchType1);

            } else {
                mUI.setPullLoadMoreCompleted();
                mUI.netErrorPage();
            }
        }
    }

    /**
     * 添加搜索点击日志
     *
     * @param key
     * @param keyType
     */
    private void addSearchClickLog(String key, String keyType) {
        DzLog.getInstance().logClick(LogConstants.MODULE_SSYM, keyType, key, null, null);
    }

    /**
     * 重新请求获取热门的接口
     *
     * @param netErrorRetryMode netErrorRetryMode
     */
    public void retryNetRequest(int netErrorRetryMode) {
        switch (netErrorRetryMode) {
            case 0:
                getHotSearchDataFromNet();
                break;
            case 1:
                break;
            case 2:
                break;
            case -10:
            default:
                break;
        }
    }

    /**
     * 清除所有历史记录
     */
    public void clearAllHistory() {
        mSearchHotAndHistory.clearHistoryList();
        mUI.disableHistoryView(mSearchHotAndHistory);
    }


    public String getSearchKey() {
        return searchKey;
    }

    public String getSearchType() {
        return searchType;
    }

    private void getPromptKeysByNet(String key) {
        searchKey = key;
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                if (keysCacheMap.containsKey(searchKey)) {
                    searchKeysBeanInfo = keysCacheMap.get(searchKey);
                    if (searchKeysBeanInfo != null) {
                        if (searchKeysBeanInfo.isExistBooks() || searchKeysBeanInfo.isExistKeys()) {
                            mUI.setKeyPromptDatas(searchKeysBeanInfo);
                            return;
                        }
                    }
                }
                searchKeysBeanInfo = new SearchKeysBeanInfo();
                //请求本地的图书
                List<BookInfo> list = DBUtils.findSearchBooksByKey(mUI.getContext(), searchKey);
                if (list != null && list.size() > 0) {
                    searchKeysBeanInfo.addLocalBooks(list);
                }
                if (searchKeysBeanInfo.isExistBooks()) {
                    mUI.setKeyPromptDatas(searchKeysBeanInfo);
                }
                //请求接口中的数据
                try {
                    final BeanSuggest suggestBean = HwRequestLib.getInstance().searchSuggestRequest(searchKey);
                    DzSchedulers.main(new Runnable() {
                        @Override
                        public void run() {
                            if (null != suggestBean) {
                                if (suggestBean.isAvailable()) {
                                    searchKeysBeanInfo.setSuggestBean(suggestBean);
                                    mUI.setKeyPromptDatas(searchKeysBeanInfo);
                                } else {
                                    mUI.clearKeyPromptDatas();
                                }
                                if (!keysCacheMap.containsKey(searchKey)) {
                                    keysCacheMap.put(searchKey, searchKeysBeanInfo);
                                } else {
                                    keysCacheMap.remove(searchKey);
                                    keysCacheMap.put(searchKey, searchKeysBeanInfo);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                }
            }
        });
    }


    /**
     * 添加本地的搜索结果日志
     *
     * @param searchString
     * @param result
     */
    private void addSearchResultLog(String searchString, BeanSearch result) {
        HashMap<String, String> map = new HashMap<>();
        map.put(LogConstants.KEY_SEARCHRESULT_KEYWORD, searchString);
        map.put(LogConstants.KEY_SEARCHRESULT_KEYTYPE, getSearchKeyType());
        String searchResult = "";
        if (null != result && !ListUtils.isEmpty(result.searchList)) {
            searchResult = searchResult + "1";
        } else {
            searchResult = searchResult + "2";
        }
        if (null != result && "4".equals(result.searchType) && !ListUtils.isEmpty(result.searchList)) {
            searchResult = searchResult + "1";
        } else {
            searchResult = searchResult + "2";
        }
        if (result != null) {
            searchResult = searchResult + "2";
            if ("5".equals(result.searchType) && !ListUtils.isEmpty(result.searchList)) {
                searchResult = searchResult + "1";
            } else {
                searchResult = searchResult + "2";
            }
        }
        map.put(LogConstants.KEY_SEARCHRESULT_RESULTTYPE, searchResult);
        DzLog.getInstance().logPv(LogConstants.KEY_SEARCHRESULT_PYTYPE, map, null);
        WhiteListWorker.setBookSourceFrom(WhiteListWorker.SEARCH_RESULT, null, mUI.getContext());

        HashMap<String, String> queueMap = new HashMap<>();
        queueMap.put(LogConstants.MAP_PI, searchString);
        QueueWorker.getInstance().addQueue(new QueueBean(LogConstants.KEY_SEARCHRESULT_PYTYPE, queueMap));
    }

    /**
     * 根据搜索条件获取搜索书籍 参数searchType keyWord
     */
    private void searchBookListTask(final boolean refresh, final String editString, final String searchType1) {
        mUI.clearEmptySearchData(refresh);
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                try {
                    final BeanSearch bookBeanListInfo = HwRequestLib.getInstance().searchRequest(editString, strPage, strTotalNum, searchType1);
                    DzSchedulers.main(new Runnable() {
                        @Override
                        public void run() {
                            if (bookBeanListInfo != null && bookBeanListInfo.isSuccess()) {
                                //添加搜索结果日志
                                addSearchResultLog(editString, bookBeanListInfo);
                            }
                            mUI.setSearchResultData(bookBeanListInfo, refresh, strPage + "");
                        }
                    });
                } catch (Exception e) {
                    mUI.setPullLoadMoreCompleted();
                    mUI.netErrorPage();
                    ALog.printStackTrace(e);
                }
            }
        });
    }
}
