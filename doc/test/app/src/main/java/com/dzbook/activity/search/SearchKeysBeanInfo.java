package com.dzbook.activity.search;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.seach.BeanSuggest;
import hw.sdk.net.bean.seach.SuggestItem;

/**
 * 搜索关键词相关提示词beaninfo
 * 由本地book列表+166接口下发的接口数据组成
 *
 * @author dongdianzhou on 2017/3/30.
 */
public class SearchKeysBeanInfo {

    /**
     * 最大显示本地数
     */
    private int maxItemLocal = 2;
    /**
     * 真实的本地书集合 展示只展示最多两本 然后跳转更多
     */
    private List<BookInfo> mBooks;
    private BeanSuggest suggestBean;

    /**
     * 构造
     */
    public SearchKeysBeanInfo() {
        mBooks = new ArrayList<BookInfo>();
    }

    /**
     * set bean
     *
     * @param beanSuggest beanSuggest
     */
    public void setSuggestBean(BeanSuggest beanSuggest) {
        this.suggestBean = beanSuggest;
    }

    /**
     * 添加本地图书bean
     *
     * @param localBooks localBooks
     */
    public void addLocalBooks(List<BookInfo> localBooks) {
        if (ListUtils.isEmpty(localBooks)) {
            return;
        }
        if (mBooks != null && mBooks.size() > 0) {
            mBooks.clear();
        }
        if (mBooks != null) {
            mBooks.addAll(localBooks);
        }
    }

    public boolean isShowMoreLocalBooks() {
        return mBooks != null && mBooks.size() > maxItemLocal;
    }

    public List<BookInfo> getShowAllLocalBooks() {
        return mBooks;
    }

    /**
     * 获取要显示的本地图书
     *
     * @return list
     */
    public List<BookInfo> getShowLocalBooks() {
        if (isExistBooks()) {
            if (mBooks.size() > maxItemLocal) {
                return mBooks.subList(0, maxItemLocal - 1);
            } else {
                return mBooks;
            }
        }
        return null;
    }

    public boolean isExistKeys() {
        return null != suggestBean && suggestBean.isAvailable();
    }

    public boolean isExistBooks() {
        return mBooks != null && mBooks.size() > 0;
    }

    /**
     * 获取推荐列表
     *
     * @return list
     */
    public List<SuggestItem> getSearchKeys() {
        if (null != suggestBean) {
            return suggestBean.list;
        }
        return null;
    }
}
