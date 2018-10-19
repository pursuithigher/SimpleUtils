package com.dzbook.view.store;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * SjMoreTitleView
 *
 * @author dongdianzhou on 2018/3/16.
 */

public class SjMoreTitleView extends RelativeLayout {

    private Context mContext;

    private TextView mTextView;
    private RelativeLayout relativeLayoutMore;

    /**
     * 构造
     *
     * @param context  context
     * @param hasFlTab hasFlTab
     */
    public SjMoreTitleView(Context context, boolean hasFlTab) {
        this(context, null, hasFlTab);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param hasFlTab hasFlTab
     */
    public SjMoreTitleView(Context context, AttributeSet attrs, boolean hasFlTab) {
        super(context, attrs);
        mContext = context;
        initView(hasFlTab);
        initData();
        setListener();
    }

    private void initView(boolean hasFlTab) {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (hasFlTab) {
            layoutParams.topMargin = DimensionPixelUtil.dip2px(mContext, 25);
        } else {
            layoutParams.topMargin = DimensionPixelUtil.dip2px(mContext, 8);
        }
        layoutParams.bottomMargin = DimensionPixelUtil.dip2px(mContext, 8);
        layoutParams.leftMargin = DimensionPixelUtil.dip2px(mContext, 16);
        layoutParams.rightMargin = DimensionPixelUtil.dip2px(mContext, 18);
        setLayoutParams(layoutParams);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_sj_moretitle, this);
        mTextView = view.findViewById(R.id.textview_title);
        TypefaceUtils.setHwChineseMediumFonts(mTextView);
        relativeLayoutMore = view.findViewById(R.id.relativelayout_more);
    }

    private void setListener() {

    }

    private void initData() {

    }

    /**
     * 绑定数据
     *
     * @param title title
     */
    public void bindData(String title) {
        mTextView.setText(title);
    }

    /**
     * 更多按钮点击事件
     *
     * @param onClickListener onClickListener
     */
    public void setMoreClickListener(OnClickListener onClickListener) {
        relativeLayoutMore.setOnClickListener(onClickListener);
    }

    /**
     * 更多按钮是否显示
     *
     * @param visible visible
     */
    public void setMoreViewVisible(int visible) {
        relativeLayoutMore.setVisibility(visible);
    }
}
