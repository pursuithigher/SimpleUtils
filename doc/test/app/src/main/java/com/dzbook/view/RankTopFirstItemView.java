package com.dzbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanRankTopResBeanInfo;

/**
 * 排行榜头条布局
 *
 * @author lizhongzhong 2018/3/10.
 */

public class RankTopFirstItemView extends RelativeLayout {

    private TextView tvText;


    /**
     * 构造
     *
     * @param context context
     */
    public RankTopFirstItemView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RankTopFirstItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
        initData();
        setListener();
    }

    private void initView(AttributeSet attrs) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_rank_top_first, this);
        tvText = view.findViewById(R.id.tv_text);
    }


    private void initData() {
        tvText.setText("");
        //        viewLine.setVisibility(View.INVISIBLE);
    }

    private void setListener() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置数据
     *
     * @param beanInfo beanInfo
     */
    public void bindData(BeanRankTopResBeanInfo.RandTopBean beanInfo) {
        if (beanInfo != null) {
            tvText.setText(beanInfo.name);
            setTag(beanInfo);
        }
    }

    /**
     * 选中
     */
    public void select() {
        setSelected(true);
        TypefaceUtils.setHwChineseMediumFonts(tvText);
        //        viewLine.setVisibility(View.VISIBLE);
    }

    /**
     * 取消选中
     */
    public void unSelect() {
        setSelected(false);
        //        viewLine.setVisibility(View.INVISIBLE);
        TypefaceUtils.setRegularFonts(tvText);
    }
}
