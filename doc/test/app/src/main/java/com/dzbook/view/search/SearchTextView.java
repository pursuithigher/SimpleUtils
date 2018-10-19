package com.dzbook.view.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.seach.BeanSearchEmpty;

/**
 * SearchTextView
 *
 * @author caimantang on 2018/4/23.
 */

public class SearchTextView extends RelativeLayout {

    private TextView tvMore, tvTitle;

    /**
     * 构造
     *
     * @param context context
     */
    public SearchTextView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_search_textview, this);
        tvMore = findViewById(R.id.tvMore);
        tvTitle = findViewById(R.id.tvTitle);

        TypefaceUtils.setHwChineseMediumFonts(tvMore);
        TypefaceUtils.setHwChineseMediumFonts(tvTitle);
    }

    /**
     * 绑定数据
     *
     * @param beanSearchEmpty beanSearchEmpty
     */
    public void bindData(BeanSearchEmpty beanSearchEmpty) {
        if (null != beanSearchEmpty) {
            if (2 == beanSearchEmpty.getType()) {
                tvTitle.setVisibility(GONE);
                tvMore.setVisibility(VISIBLE);
                tvMore.setText(beanSearchEmpty.getMsg());
            } else {
                tvTitle.setVisibility(VISIBLE);
                tvMore.setVisibility(GONE);
                tvTitle.setText(beanSearchEmpty.getMsg());
            }
        }
    }
}
