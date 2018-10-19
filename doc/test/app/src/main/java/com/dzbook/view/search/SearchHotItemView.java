package com.dzbook.view.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.SearchPresenterImpl;
import com.dzbook.utils.ThirdPartyLog;
import com.ishugui.R;

import hw.sdk.net.bean.seach.BeanKeywordHotVo;

/**
 * 热词Item
 *
 * @author dongdianzhou on 2017/9/7.
 */

public class SearchHotItemView extends RelativeLayout {

    private TextView mTextViewMark;
    private TextView mTextViewContent;

    private BeanKeywordHotVo bookstoreSearchKeyBean;

    private SearchPresenterImpl mSearchPresenter;
    private View mReMain;

    /**
     * 构造
     *
     * @param context context
     */
    public SearchHotItemView(Context context) {
        super(context);
        initView();
        initData();
        setListener();
    }

    public void setSearchPresenter(SearchPresenterImpl searchPresenter) {
        this.mSearchPresenter = searchPresenter;
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.SEACH_PAGE_HOT_ID, null, 1);
                String key = bookstoreSearchKeyBean.name.trim();
                //                ALog.eDongdz("当前点击的热门标签文本是： " + key);
                if (!TextUtils.isEmpty(key) && null != mSearchPresenter) {
                    mSearchPresenter.searchFixedKey(key, LogConstants.ZONE_SSYM_RMSS, "", false);
                }
            }
        });
    }

    private void initData() {

    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_searchhot_item, this);
        mTextViewMark = view.findViewById(R.id.textview_mark);
        mTextViewContent = view.findViewById(R.id.textview_content);
        mReMain = view.findViewById(R.id.re_main);
    }

    /**
     * 绑定数据
     *
     * @param bookstoreSearchKeyBean1 bookstoreSearchKeyBean
     * @param position                position
     * @param isNeedExchange          isNeedExchange
     */
    public void bindData(BeanKeywordHotVo bookstoreSearchKeyBean1, int position, boolean isNeedExchange) {
        this.bookstoreSearchKeyBean = bookstoreSearchKeyBean1;
        mTextViewContent.setText(bookstoreSearchKeyBean1.name);
        mTextViewMark.setText((position + 1) + "");
        mTextViewMark.setVisibility(VISIBLE);
        if (position < 4) {
            mTextViewMark.setBackgroundResource(R.drawable.dz_radius_red);
        } else {
            mTextViewMark.setBackgroundResource(R.drawable.dz_radius_grey);
        }
//        if (isNeedExchange) {
//            ViewGroup.LayoutParams layoutParams = mReMain.getLayoutParams();
//            layoutParams.height = (int) getResources().getDimension(R.dimen.dp_32);
//            layoutParams.width = LayoutParams.MATCH_PARENT;
//            mReMain.setLayoutParams(layoutParams);
//            mReMain.setMinimumHeight((int) getResources().getDimension(R.dimen.dp_32));
//        } else {
            ViewGroup.LayoutParams layoutParams = mReMain.getLayoutParams();
            layoutParams.height = (int) getResources().getDimension(R.dimen.dp_32);
            layoutParams.width = LayoutParams.MATCH_PARENT;
            mReMain.setLayoutParams(layoutParams);
            mReMain.setMinimumHeight((int) getResources().getDimension(R.dimen.dp_32));
//        }
    }
}
