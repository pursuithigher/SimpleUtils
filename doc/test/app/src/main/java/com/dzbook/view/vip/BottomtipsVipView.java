package com.dzbook.view.vip;

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
 * 温馨提示
 *
 * @author gavin
 */
public class BottomtipsVipView extends LinearLayout {
    private TextView mTvUserName;

    /**
     * 构造
     *
     * @param context context
     */
    public BottomtipsVipView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public BottomtipsVipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {
        String userId = SpUtil.getinstance(getContext()).getUserID();
        String content = mTvUserName.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            content = content.replace("#userId#", userId);
        }
        mTvUserName.setText(content);
    }

    private void initView() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = DimensionPixelUtil.dip2px(getContext(), 16);
        params.rightMargin = DimensionPixelUtil.dip2px(getContext(), 16);
        params.topMargin = DimensionPixelUtil.dip2px(getContext(), 25);
        params.bottomMargin = DimensionPixelUtil.dip2px(getContext(), 25);
        setOrientation(VERTICAL);
        setLayoutParams(params);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_vip_tips, this);
        mTvUserName = view.findViewById(R.id.tv_userId_msg);
        TextView textView = view.findViewById(R.id.tv_bottom_tips);
        TypefaceUtils.setHwChineseMediumFonts(textView);
    }

    /**
     * 绑定数据
     *
     * @param userName userName
     */
    public void bindData(String userName) {
        mTvUserName.setText(userName);
    }
}
