package com.dzbook.recharge.order;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.UI.LotOrderUI;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzpay.recharge.netbean.LotOrderPageBean;
import com.ishugui.R;

/**
 * LotOrderItemView
 * @author lizhongzhong 2017/8/3.
 */

public class LotOrderItemView extends RelativeLayout {

    private RadioButton radiobuttonSwBg;
    private TextView tvCornerRate, tvLotsTips;
    private TextView relativeCorner;


    private LotOrderUI lotOrderUI;

    /**
     * LotOrderItemView
     * @param context context
     */
    public LotOrderItemView(Context context) {
        this(context, null);
    }

    /**
     * LotOrderItemView
     * @param context context
     * @param attrs attrs
     */
    public LotOrderItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
    }


    private void initView() {
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 60));
        setLayoutParams(params);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dz_recharge_lot_order_item, this);
        radiobuttonSwBg = view.findViewById(R.id.radiobutton_sw_bg);
        tvCornerRate = view.findViewById(R.id.tv_corner_rate);
        tvLotsTips = view.findViewById(R.id.tv_lotsTips);
        relativeCorner = view.findViewById(R.id.relative_corner);
    }

    private void initData() {

    }

    private void setListener(final LotOrderPageBean bean, final int position) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lotOrderUI.onSelected(position, bean);
                lotOrderUI.setSelection(position);
            }
        });

    }

    public void setLotOrderUI(LotOrderUI orderUI) {
        this.lotOrderUI = orderUI;
    }

    /**
     * bindData
     * @param bean bean
     * @param position position
     * @param isSelected isSelected
     */
    public void bindData(LotOrderPageBean bean, int position, boolean isSelected) {
        if (bean == null) {
            setVisibility(View.INVISIBLE);
            return;
        }
        setVisibility(View.VISIBLE);

        setListener(bean, position);
        setText(tvCornerRate, bean.corner);
        setText(tvLotsTips, bean.tips);
        radiobuttonSwBg.setChecked(isSelected);
        tvLotsTips.setSelected(isSelected);
    }


    private void setText(TextView tv, CharSequence text) {
        if (tv == null) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            relativeCorner.setVisibility(View.GONE);
        } else {

            tv.setText(text);
            tv.setVisibility(View.VISIBLE);
        }
    }

}
