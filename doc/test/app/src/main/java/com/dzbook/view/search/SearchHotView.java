package com.dzbook.view.search;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dzbook.activity.search.SearchHotAdapter;
import com.dzbook.mvp.presenter.SearchPresenterImpl;
import com.dzbook.view.SelfAdapterGridLayoutManager;
import com.ishugui.R;

import java.util.Collections;
import java.util.List;

import hw.sdk.net.bean.seach.BeanKeywordHotVo;

/**
 * 热词
 *
 * @author dongdianzhou on 2017/3/29.
 */

public class SearchHotView extends LinearLayout {

    private static final int SEARCH_HOT_NUM = 10;

    private Context mContext;
    private SearchHotTitleView mHotTitleView;
    private RecyclerView mRecyclerViewhot;
    private SearchPresenterImpl mSearchPresenter;

    private SearchHotAdapter mAdapter;

    /***
     * 热门搜索一次最多显示6条数据且可以换一换
     * start：hotDataIndex*6
     * end：（hotDataIndex+1）* 6 -1
     */
    private int hotDataIndex = 0;

    private List<BeanKeywordHotVo> mHotList;

    /**
     * 构造
     *
     * @param context context
     */
    public SearchHotView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SearchHotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        setListener();
    }

    public void setSearchPresenter(SearchPresenterImpl searchPresenter) {
        this.mSearchPresenter = searchPresenter;
    }

    private void setListener() {
        mHotTitleView.getOperView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BeanKeywordHotVo> pageListData = getIndexList();
                mAdapter.addItems(pageListData);
            }
        });
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_searchhot, this);
        mHotTitleView = view.findViewById(R.id.hottitleview);
        mRecyclerViewhot = view.findViewById(R.id.recyclerview_hot);
        mRecyclerViewhot.setLayoutManager(new SelfAdapterGridLayoutManager(getContext(), 2));
        mAdapter = new SearchHotAdapter();
        mRecyclerViewhot.setAdapter(mAdapter);
    }

    /**
     * 获取到热门搜索的搜索段list
     *
     * @return
     */
    private List<BeanKeywordHotVo> getIndexList() {
        if (null == mHotList || mHotList.isEmpty()) {
            return null;
        }
        int start = hotDataIndex * SEARCH_HOT_NUM;
        int end = (hotDataIndex + 1) * SEARCH_HOT_NUM;
        int size = mHotList.size();
        if (start >= size) {
            hotDataIndex = 0;
            start = hotDataIndex * SEARCH_HOT_NUM;
            end = (hotDataIndex + 1) * SEARCH_HOT_NUM;
        }
        end = Math.min(end, size);
        hotDataIndex++;
        return mHotList.subList(start, end);
    }

    /**
     * 绑定数据
     *
     * @param hotlist        hotlist
     * @param isNeedExchange 是否支持换一换
     */
    public void bindData(List<BeanKeywordHotVo> hotlist, boolean isNeedExchange) {
        if (isNeedExchange) {
            mHotTitleView.setVisibility(VISIBLE);
        } else {
            mHotTitleView.setVisibility(GONE);
        }
        hotDataIndex = 0;
        mHotList = hotlist;
        if (null != hotlist) {
            Collections.shuffle(hotlist);
        }
        mAdapter.setSearchPresenter(mSearchPresenter, isNeedExchange);
        if (mHotList != null && mHotList.size() > 0) {
            List<BeanKeywordHotVo> pageListData = getIndexList();
            mAdapter.addItems(pageListData);
        }
    }
}
