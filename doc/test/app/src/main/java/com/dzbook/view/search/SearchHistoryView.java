package com.dzbook.view.search;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.search.SearchHotAndHistoryBeanInfo;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.SearchPresenterImpl;
import com.dzbook.utils.ThirdPartyLog;
import com.ishugui.R;

import java.util.List;

/**
 * 搜索历史
 *
 * @author dongdianzhou on 2017/3/29.
 */

public class SearchHistoryView extends LinearLayout {

    private Context mContext;
    private SearchHotTitleView mHotTitleView;
    private FlowLayout mFlowLayoutKeys;
    private SearchPresenterImpl mSearchPresenter;

    private SearchHotAndHistoryBeanInfo mSearchHotAndHistory;

    /**
     * 构造
     *
     * @param context context
     */
    public SearchHistoryView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SearchHistoryView(Context context, @Nullable AttributeSet attrs) {
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
                mSearchPresenter.clearAllHistory();
            }
        });
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_searchistory, this);
        mHotTitleView = view.findViewById(R.id.hottitleview);
        mFlowLayoutKeys = view.findViewById(R.id.flowlayout_hotkey);
    }

    /**
     * 绑定数据
     *
     * @param searchHotAndHistory searchHotAndHistory
     */
    public void bindData(SearchHotAndHistoryBeanInfo searchHotAndHistory) {
        mSearchHotAndHistory = searchHotAndHistory;
        if (mSearchHotAndHistory.isExistHistoryList()) {
            setFlowData(mSearchHotAndHistory.getHistoryList());
        }
    }

    /**
     * 设置热门搜索view的数据
     */
    private void setFlowData(List<String> pageListData) {
        if (pageListData != null && pageListData.size() > 0) {
            mFlowLayoutKeys.removeAllData();
            for (String history : pageListData) {
                final TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_searchhistory_textview, null);
                String trim = history.trim();
                view.setText(trim);
                mFlowLayoutKeys.addView(view);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ThirdPartyLog.onEventValueOldClick(mContext, ThirdPartyLog.SEACH_PAGE_HISTORY_ID, null, 1);
                        //                        ALog.eDongdz("当前点击的热门标签文本是： " + view.getText().toString().trim());
                        String searchKey = view.getText().toString().trim();
                        if (!TextUtils.isEmpty(searchKey)) {
                            mSearchPresenter.searchFixedKey(searchKey, LogConstants.ZONE_SSYM_CGSS, "", false);
                        }
                    }
                });
            }
        }
    }
}
