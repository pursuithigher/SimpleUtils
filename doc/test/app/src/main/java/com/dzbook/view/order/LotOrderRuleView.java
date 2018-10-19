package com.dzbook.view.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * LotOrderRuleView
 *
 * @author lizz 2018/4/16.
 */

public class LotOrderRuleView extends RelativeLayout {

    private Context mContext;


    private TextView tvLotOrderRule;

    /**
     * 构造
     *
     * @param context context
     */
    public LotOrderRuleView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public LotOrderRuleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }


    private void initView() {
        int paddingTop = DimensionPixelUtil.dip2px(mContext, 20);
        int paddingBottom = DimensionPixelUtil.dip2px(mContext, 20);
        int padding = DimensionPixelUtil.dip2px(mContext, 16);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        setPadding(padding, paddingTop, padding, paddingBottom);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lot_order_rule, this);
        tvLotOrderRule = view.findViewById(R.id.tv_lot_order_rule);
    }

    private void initData() {

    }

    private void setListener() {
        tvLotOrderRule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CenterDetailActivity.show(getContext(), RequestCall.urlLotDownloadDiscount(), getResources().getString(R.string.str_lot_download_rule));
            }
        });
    }


}
