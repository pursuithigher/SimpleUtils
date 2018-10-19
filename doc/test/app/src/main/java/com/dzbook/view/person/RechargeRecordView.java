package com.dzbook.view.person;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import hw.sdk.net.bean.record.RechargeRecordBean;

/**
 * RechargeRecordView
 *
 * @author dongdianzhou on 2017/4/6.
 */

public class RechargeRecordView extends RelativeLayout {

    private TextView mTextviewContent;
    private TextView mTextviewTitle;
    private TextView mTextViewTime;
    private View mViewLine;

    /**
     * 构造
     *
     * @param context context
     */
    public RechargeRecordView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RechargeRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        if (!isClickable()) {
            setClickable(true);
        }
    }

    private void initData() {

    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        int paddingLeft = DimensionPixelUtil.dip2px(getContext(), 16);
        setPadding(paddingLeft, 0, paddingLeft, 0);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 64)));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_rechargerecord_item, this);
        mTextviewContent = view.findViewById(R.id.textview_content);
        mTextViewTime = view.findViewById(R.id.textView_time);
        mTextviewTitle = view.findViewById(R.id.textview_title);
        mViewLine = view.findViewById(R.id.view_line);
    }


    /**
     * 绑定数据
     *
     * @param recordBeanInfo recordBeanInfo
     * @param isShowEndLine  isShowEndLine
     */
    public void bindData(RechargeRecordBean recordBeanInfo, boolean isShowEndLine) {
        if (recordBeanInfo != null) {
            mTextviewContent.setText(recordBeanInfo.amount);
            mTextViewTime.setText(recordBeanInfo.reTime);
            mTextviewTitle.setText(recordBeanInfo.des);
            int visibility = isShowEndLine ? GONE : VISIBLE;
            if (mViewLine.getVisibility() != visibility) {
                mViewLine.setVisibility(visibility);
            }
        }
    }
}
