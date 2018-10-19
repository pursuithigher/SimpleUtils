package com.dzbook.view.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.ishugui.R;

/***
 * 位于顶部的网络异常提示
 * @author wangjianchen
 */
public class NetErrorTopView extends LinearLayout {
    /**
     * 错误
     */
    public static final int TYPE_SERVER_ERROR = 1;
    /**
     * 无数据
     */
    public static final int TYPE_SERVER_EMPTY = 2;
    /**
     * 无网
     */
    public static final int TYPE_NO_NET = 3;

    private FreshClickListener freshClickListener;
    private int netErrorIconSize;
    private int netErrorIconPad;
    private int mType = -1;
    private Paint paint;

    /**
     * NetErrorTopView
     * @param context context
     */
    public NetErrorTopView(Context context) {
        super(context);
        init();
    }

    /**
     * NetErrorTopView
     * @param context context
     * @param attrs attrs
     */
    public NetErrorTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        netErrorIconPad = DimensionPixelUtil.dip2px(getContext(), 9);
        netErrorIconSize = DimensionPixelUtil.dip2px(getContext(), 24);
        initDefault();
        initView();
    }

    private void initDefault() {
        setOrientation(HORIZONTAL);
        setPadding(DimensionPixelUtil.dip2px(getContext(), 16), 0, DimensionPixelUtil.dip2px(getContext(), 12), 0);
        setBackgroundColor(Color.parseColor("#ffffff"));
        paint = new Paint();
        paint.setColor(Color.parseColor("#19FF0000"));
    }

    private void initView() {
        netError();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mType) {
                    case TYPE_SERVER_ERROR:
                    case TYPE_SERVER_EMPTY:
                        if (null != freshClickListener) {
                            freshClickListener.onFresh(v);
                        }
                        break;
                    case TYPE_NO_NET:
                        //没网
                    default:
                        NetworkUtils.getInstance().setNetSetting(getContext());
                        break;

                }
            }
        });
    }

    /**
     * serverError
     */
    public void serverError() {
        removeAllViews();
        errorHint(R.string.server_error_tip);
    }

    /**
     * netError
     */
    public void netError() {
        removeAllViews();
        errorHint(R.string.hw_network_connection_no);
        toSetHint();
    }

    /**
     * FreshClickListener
     */
    public interface FreshClickListener {
        /**
         * 点击设置按钮
         *
         * @param v view
         */
        void onFresh(View v);

    }

    public void setOnFreshClickListener(final FreshClickListener clickListener) {
        this.freshClickListener = clickListener;
    }

    /**
     * emptyError
     */
    public void emptyError() {
        removeAllViews();
        errorHint(R.string.server_empty_tip);
    }

    private void errorHint(int resString) {
        TextView netErrorView = new TextView(getContext());
        netErrorView.setText(getResources().getString(resString));
        netErrorView.setTextSize(15);
        netErrorView.setTextColor(Color.parseColor("#FF3320"));
        netErrorView.setEllipsize(TextUtils.TruncateAt.END);
        netErrorView.setMaxLines(1);
        netErrorView.setGravity(Gravity.CENTER_VERTICAL);
        Drawable drawable = CompatUtils.getDrawable(getContext(), R.drawable.hw_net_error_hint);
        drawable.setBounds(0, 0, netErrorIconSize, netErrorIconSize);
        netErrorView.setCompoundDrawables(drawable, null, null, null);
        netErrorView.setCompoundDrawablePadding(netErrorIconPad);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.weight = 1;
        layoutParams.rightMargin = DimensionPixelUtil.dip2px(getContext(), 12);
        addView(netErrorView, layoutParams);
    }

    private void toSetHint() {
        TextView netSetView = new TextView(getContext());
        netSetView.setText(getResources().getString(R.string.str_set_up_the_network));
        netSetView.setTextSize(15);
        netSetView.setTextColor(Color.parseColor("#7f1a1a1a"));
        netSetView.setSingleLine();
        netSetView.setGravity(Gravity.CENTER_VERTICAL);
        Drawable drawable = CompatUtils.getDrawable(getContext(), R.drawable.hw_arrow_right);
        drawable.setBounds(0, 0, netErrorIconPad, netErrorIconSize);
        netSetView.setCompoundDrawables(null, null, drawable, null);
        netSetView.setCompoundDrawablePadding(netErrorIconPad);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        addView(netSetView, layoutParams);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
        super.dispatchDraw(canvas);
    }

    /**
     * setType
     * @param type type
     */
    public void setType(int type) {
        mType = type;
        switch (type) {
            case TYPE_SERVER_ERROR:
                serverError();
                break;
            case TYPE_SERVER_EMPTY:
                emptyError();
                break;
            case TYPE_NO_NET:
                netError();
                break;
            default:
                break;
        }
    }
}
