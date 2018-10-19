package com.dzbook.view.order;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * OrderTitle
 */
public class OrderTitle extends LinearLayout {
    private TextView mTvTitle;
    private TextView mTvCostPrice;
    private TextView mTvPayPrice;
    private TextView mTvPayPriceTip;
    private TextView mTvBalance;

    /**
     * 构造
     *
     * @param context context
     */
    public OrderTitle(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public OrderTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public OrderTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setLayoutParams(layoutParams);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.pack_order_title, this, true);
        mTvTitle = findViewById(R.id.tv_title);
        mTvCostPrice = findViewById(R.id.tv_cost_price);
        mTvPayPriceTip = findViewById(R.id.tv_tv_pay_price_tip);
        mTvPayPrice = findViewById(R.id.tv_pay_price);
        mTvBalance = findViewById(R.id.tv_balance);
        TypefaceUtils.setHwChineseMediumFonts(mTvTitle);
        TypefaceUtils.setHwChineseMediumFonts(mTvPayPriceTip);
        TypefaceUtils.setHwChineseMediumFonts(mTvPayPrice);
    }

    /**
     * 绑定数据
     *
     * @param title     title
     * @param costPrice costPrice
     * @param payPrice  payPrice
     * @param balance   balance
     */
    public void bindData(String title, String costPrice, String payPrice, String balance) {
        setTextView(mTvTitle, title);
        setTextView(mTvCostPrice, costPrice);
        setTextView(mTvPayPrice, payPrice);
        setTextView(mTvBalance, balance);

    }

    private void setTextView(TextView textView, String msg) {
        if (null != textView && !TextUtils.isEmpty(msg)) {
            textView.setText(msg);
        }

    }
}
