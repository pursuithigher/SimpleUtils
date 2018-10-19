package com.dzbook.view.order;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * RechargeBottomTipsView
 *
 * @author lizz 2018/4/16.
 */

public class RechargeBottomTipsView extends LinearLayout {

    private Context mContext;

    private TextView tvUseridMsg;

    /**
     * 构造
     *
     * @param context context
     */
    public RechargeBottomTipsView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RechargeBottomTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
    }

    private void initView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        setOrientation(VERTICAL);
        int topPadding = DimensionPixelUtil.dip2px(mContext, 17);
        setPadding(0, topPadding, 0, 0);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dz_recharge_bottom_tips, this);
        tvUseridMsg = view.findViewById(R.id.tv_userId_msg);

        TextView tvTips = view.findViewById(R.id.tv_tips);

        TypefaceUtils.setHwChineseMediumFonts(tvTips);
    }

    private void initData() {
        String userId = SpUtil.getinstance(mContext).getUserID();

        String content = tvUseridMsg.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            content = content.replace("#userId#", userId);
        }
        tvUseridMsg.setText(content);
    }

}
