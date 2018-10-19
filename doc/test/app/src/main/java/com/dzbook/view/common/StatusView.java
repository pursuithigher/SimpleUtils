package com.dzbook.view.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.ishugui.R;

import huawei.widget.HwProgressBar;

import static android.widget.LinearLayout.VERTICAL;

/**
 * 状态切换view
 *
 * @author wangjianchen
 */
public class StatusView extends ScrollView {

    private int iconSize;
    private int iconPadding;
    private int buttonSize;
    private int buttonWidth;
    private int statusImgViewMargin;
    private int statusIndex;
    private boolean isNetError;

    private HwProgressBar loadingView;
    private ImageView statusImgView;
    private TextView txtView;
    private Button setButtonView;
    private LinearLayout linearLayout;
    private SetClickListener clickListener;
    private NetErrorClickListener netErrorClickListener;

    /**
     * 构造
     *
     * @param context context
     */
    public StatusView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        statusIndex = 1;
    }

    /**
     * 初始化全局整体View
     */
    private void initView() {
        if (linearLayout == null) {
            synchronized (this) {
                initContentView();
            }
        }
        setVisibility(VISIBLE);

    }

    private void initContentView() {
        // 需要锁内外双重判断
        if (linearLayout == null) {
            setFillViewport(true);
            setVerticalScrollBarEnabled(false);
            setBackgroundColor(Color.parseColor("#ffffff"));
            linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(VERTICAL);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            removeAllViews();
            addView(linearLayout, layoutParams);
            linearLayout.setId(R.id.status_setting);
            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (netErrorClickListener != null && isNetError) {
                        netErrorClickListener.onNetErrorEvent(v);
                    }
                }
            });
            initWeightView();
            initTxtView();
            initWeightView();
        }
    }

    /**
     * 构建公共的中间位置图片显示控件
     */
    private void initImage(Drawable drawable) {
        if (statusImgView == null) {
            if (iconSize == 0) {
                iconSize = DimensionPixelUtil.dip2px(getContext(), 72);
            }
            if (statusImgViewMargin == 0) {
                statusImgViewMargin = DimensionPixelUtil.dip2px(getContext(), 50);
            }
            statusImgView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iconSize, iconSize);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.topMargin = statusImgViewMargin;
            statusImgView.setLayoutParams(layoutParams);
        }
        statusImgView.setImageDrawable(drawable);
        statusImgView.setVisibility(VISIBLE);
        linearLayout.removeView(statusImgView);
        linearLayout.addView(statusImgView, statusIndex);
    }

    /**
     * 移除公共的中间位置图片显示控件
     */
    private void removeImage() {
        if (statusImgView != null) {
            linearLayout.removeView(statusImgView);
            statusImgView = null;
        }
    }

    /**
     * 中间图文的"图形"部分。图形可能为loadingView，可能为静态图片。
     */
    private void initLoadingView() {
        if (loadingView == null) {
            if (iconSize == 0) {
                iconSize = DimensionPixelUtil.dip2px(getContext(), 72);
            }
            if (statusImgViewMargin == 0) {
                statusImgViewMargin = DimensionPixelUtil.dip2px(getContext(), 50);
            }
            loadingView = (HwProgressBar) View.inflate(getContext(), R.layout.view_loading_large, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iconSize, iconSize);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.topMargin = statusImgViewMargin;
            loadingView.setLayoutParams(layoutParams);
        }
        linearLayout.removeView(loadingView);
        linearLayout.addView(loadingView, statusIndex);
        loadingView.setVisibility(VISIBLE);
    }

    /**
     * 中间图文的"图形"部分。图形可能为loadingView，可能为静态图片。
     */
    private void removeLoadingView() {
        if (loadingView != null) {
            linearLayout.removeView(loadingView);
            loadingView.setVisibility(GONE);
            loadingView = null;
        }
    }

    /**
     * 初始化中间文案控件
     */
    private void initTxtView() {
        if (iconPadding == 0) {
            iconPadding = DimensionPixelUtil.dip2px(getContext(), 1);
        }
        if (statusImgViewMargin == 0) {
            statusImgViewMargin = DimensionPixelUtil.dip2px(getContext(), 50);
        }
        txtView = new TextView(getContext());
        txtView.setIncludeFontPadding(false);
        txtView.setPadding(0, iconPadding, 0, 0);
        txtView.setTextColor(CompatUtils.getColor(getContext(), R.color.color_50_1A1A1A));
        txtView.setTextSize(13);
        txtView.setMaxLines(2);
        txtView.setEllipsize(TextUtils.TruncateAt.END);
        txtView.setText(R.string.loadContent);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.bottomMargin = statusImgViewMargin;
        linearLayout.removeView(txtView);
        linearLayout.addView(txtView, layoutParams);
    }

    /**
     * 初始化底部控件
     *
     * @param buttonStr buttonStr
     */
    public void initBottomButton(String buttonStr) {
        if (setButtonView == null) {
            if (buttonSize == 0) {
                buttonSize = DimensionPixelUtil.dip2px(getContext(), 32);
            }
            if (buttonWidth == 0) {
                //UX提出的这种按钮为屏幕的宽度的一半
                buttonWidth = DeviceInfoUtils.getInstanse().getWidthReturnInt() / 2;
            }

            Button btn2 = new Button(getContext());
            btn2.setPadding(0, 0, 0, 0);
            btn2.setGravity(Gravity.CENTER);
            btn2.setId(R.id.status_setting);
            btn2.setTextSize(15);
            btn2.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_191919));
            btn2.setBackground(CompatUtils.getDrawable(getContext(), R.drawable.shape_status_view_button));
            btn2.setText(getResources().getString(R.string.str_set_up_the_network));
            btn2.setSingleLine();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(buttonWidth, buttonSize);
            int margin = buttonSize / 2;
            layoutParams.setMargins(0, 0, 0, margin);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            btn2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setButtonView != null && getResources().getString(R.string.str_set_up_the_network).equals(setButtonView.getText().toString())) {
                        NetworkUtils.getInstance().setNetSetting(getContext());
                    } else if (clickListener != null) {
                        clickListener.onSetEvent(v);
                    }
                }
            });
            btn2.setLayoutParams(layoutParams);
            setButtonView = btn2;
        }
        setButtonView.setText(buttonStr);
        setButtonView.setVisibility(VISIBLE);
        linearLayout.removeView(setButtonView);
        linearLayout.addView(setButtonView);
    }

    /**
     * 移除掉底部的Button
     */
    public void removeBottomButton() {
        if (setButtonView != null) {
            linearLayout.removeView(setButtonView);
            setButtonView = null;
        }
    }

    /**
     * 初始化Weight布局
     */
    private void initWeightView() {
        View view = new View(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1, 0);
        layoutParams.weight = 1;
        linearLayout.removeView(view);
        linearLayout.addView(view, layoutParams);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            return;
        }
        isNetError = false;
        if (visibility == View.GONE && loadingView != null) {
            loadingView.setVisibility(GONE);
        }
        loadingView = null;
        linearLayout = null;
        statusImgView = null;
        setButtonView = null;
    }

    /**
     * 展示Loading状态
     */
    public void showLoading() {
        isNetError = false;
        initView();
        removeBottomButton();
        removeImage();
        initLoadingView();
        setHintStr(getResources().getString(R.string.loadContent));
    }

    /**
     * 外界根据type调用公共的error类型
     *
     * @param type type
     */
    public void showNetError(int type) {
        isNetError = true;
        setPublicType(type);
    }

    /**
     * 展示NetError状态
     */
    public void showNetError() {
        isNetError = true;
        if (!NetworkUtils.getInstance().checkNet()) {
            setPublicType(NetErrorTopView.TYPE_NO_NET);
        } else {
            setPublicType(NetErrorTopView.TYPE_SERVER_ERROR);
        }
    }

    /**
     * 展示NetError状态
     *
     * @param desc desc
     */
    public void showNetError(String desc) {
        isNetError = true;
        showNetError(desc, getResources().getString(R.string.str_set_up_the_network));
    }

    /**
     * 展示NetError状态
     *
     * @param desc desc
     * @param set  set
     */
    public void showNetError(String desc, String set) {
        isNetError = true;
        initView();
        removeLoadingView();
        if (TextUtils.isEmpty(set)) {
            removeBottomButton();
        } else {
            initBottomButton(set);
        }
        initImage(CompatUtils.getDrawable(getContext(), R.drawable.hw_no_network));
        setHintStr(desc);
    }

    /**
     * 展示showSuccess状态
     */
    public void showSuccess() {
        isNetError = false;
        setVisibility(GONE);
    }


    /**
     * 更新中间默认文案
     *
     * @param str str
     */
    public void setHintStr(String str) {
        txtView.setText(str);
    }

    /**
     * 展示Empty状态
     */
    public void showEmpty() {
        isNetError = false;
        String hint = getContext().getString(R.string.string_empty_hint);
        String setStr = "";
        showEmpty(hint, setStr);
    }

    /**
     * 展示Empty状态
     *
     * @param hint hint
     */
    public void showEmpty(String hint) {
        isNetError = false;
        showEmpty(hint, "");
    }

    /**
     * 展示Empty状态
     *
     * @param hint   hint
     * @param setStr setStr
     */
    public void showEmpty(String hint, String setStr) {
        isNetError = false;
        Drawable drawable = CompatUtils.getDrawable(getContext(), R.drawable.hw_empty_default);
        showEmpty(hint, setStr, drawable);
    }

    /**
     * 展示Empty状态
     *
     * @param hint     hint
     * @param drawable drawable
     */
    public void showEmpty(String hint, Drawable drawable) {
        isNetError = false;
        showEmpty(hint, "", drawable);
    }

    /**
     * 展示Empty状态
     *
     * @param hint     hint
     * @param drawable drawable
     * @param setStr   setStr
     */
    public void showEmpty(String hint, String setStr, Drawable drawable) {
        isNetError = false;
        initView();
        removeLoadingView();
        if (TextUtils.isEmpty(setStr)) {
            removeBottomButton();
        } else {
            initBottomButton(setStr);
        }
        initImage(drawable);
        setHintStr(hint);
    }

    /**
     * 底部点击事件对外暴露方法
     *
     * @param clickListener1 clickListener
     */
    public void setClickSetListener(final SetClickListener clickListener1) {
        this.clickListener = clickListener1;
    }

    /**
     * 网络异常时候点击屏幕刷新
     *
     * @param netErrorClickListener netErrorClickListener
     */
    public void setNetErrorClickListener(final NetErrorClickListener netErrorClickListener) {
        this.netErrorClickListener = netErrorClickListener;
    }

    /**
     * 底部点击事件
     */
    public interface SetClickListener {
        /**
         * 点击设置按钮
         *
         * @param v view
         */
        void onSetEvent(View v);
    }

    /**
     * 网络异常时点击屏幕
     */
    public interface NetErrorClickListener {
        /**
         * 点击屏幕，刷新网络
         *
         * @param v view
         */
        void onNetErrorEvent(View v);
    }

    /**
     * 错误类型
     *
     * @param type type
     */
    public void setPublicType(int type) {
        switch (type) {
            case NetErrorTopView.TYPE_SERVER_ERROR:
                showNetError(getResources().getString(R.string.server_error_tip), "");
                break;
            case NetErrorTopView.TYPE_SERVER_EMPTY:
                showNetError(getResources().getString(R.string.server_empty_tip), "");
                break;
            case NetErrorTopView.TYPE_NO_NET:
                showNetError(getResources().getString(R.string.hw_network_connection_no));
                break;
            default:
                break;
        }
    }
}
