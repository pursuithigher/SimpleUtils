package com.dzbook.mvp.UI;

import com.dzbook.activity.search.SearchHotAndHistoryBeanInfo;
import com.dzbook.activity.search.SearchKeysBeanInfo;
import com.dzbook.mvp.BaseUI;

import hw.sdk.net.bean.seach.BeanSearch;

/**
 * 搜索
 *
 * @author dongdianzhou on 2017/3/29.
 */

public interface SearchUI extends BaseUI {
    /**
     * 显示无连接view
     *
     * @param requestmode requestmode
     */
    void showNoNetConnectView(final int requestmode);

    /**
     * 加载中dialog
     */
    void showLoadDataDialog();

    /**
     * 取消显示加载中dialog
     */
    void dismissLoadDataDialog();

    /**
     * 设置编辑框热词
     *
     * @param hotKey hotKey
     */
    void setEditTextData(String hotKey);

    /**
     * 搜索历史/热门搜索设置edittext的text
     *
     * @param tags           tags
     * @param isGetKeyPrompt isGetKeyPrompt
     */
    void setEditTextData(String tags, boolean isGetKeyPrompt);

    /**
     * 设置key提示词列表
     *
     * @param searchKeysBeanInfo searchKeysBeanInfo
     */
    void setKeyPromptDatas(SearchKeysBeanInfo searchKeysBeanInfo);

    /**
     * 刷新搜索历史：搜索接口回调
     *
     * @param b b
     */
    void referenceHistory(SearchHotAndHistoryBeanInfo b);

    /**
     * 清理历史隐藏历史view
     *
     * @param searchHotAndHistory searchHotAndHistory
     */
    void disableHistoryView(SearchHotAndHistoryBeanInfo searchHotAndHistory);

    /***
     * 设置热搜和历史数据
     * @param mSearchHotAndHistory mSearchHotAndHistory
     */
    void setHotAndHistoryData(SearchHotAndHistoryBeanInfo mSearchHotAndHistory);

    /**
     * 获取搜索结果，用于打点
     *
     * @return boolean
     */
    boolean getSearchResultType();

    /**
     * 隐藏键盘
     */
    void hideKeyboard();

    /**
     * 显示键盘
     */
    void showKeyboard();

    /**
     * 下拉刷新加载结束
     */
    void setPullLoadMoreCompleted();

    /**
     * 清空当次搜索数据
     *
     * @param refresh refresh
     */
    void clearEmptySearchData(boolean refresh);

    /**
     * 设置搜索数据
     *
     * @param result  result
     * @param refresh refresh
     * @param strPage strPage
     */
    void setSearchResultData(BeanSearch result, boolean refresh, String strPage);

    /**
     * 联想词为空的时候 需要清空之前的数据
     */
    void clearKeyPromptDatas();

    /**
     * 网络错误页面
     */
    void netErrorPage();
}
