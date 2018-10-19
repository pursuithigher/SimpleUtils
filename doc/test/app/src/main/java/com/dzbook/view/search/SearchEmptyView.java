package com.dzbook.view.search;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ishugui.R;

/**
 * 搜索空白页
 *
 * @author dongdianzhou on 2017/11/7.
 */

public class SearchEmptyView extends LinearLayout {

    /**
     * 构造
     *
     * @param context context
     */
    public SearchEmptyView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SearchEmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
    }

    private void initData() {

    }

    private void initView() {
        setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.header_search_result_empty, this, true);

    }
}
