package com.dzbook.view.shelf;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.web.ActionEngine;
import com.ishugui.R;

import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * 书架签到
 */
public class ShelfSignInView extends LinearLayout {

    private Context mContext;

    private ProgressBar progressBar;
    private TextView textViewTime, textViewMinute, textViewReadTips;
    private Button buttonSignIn;
    private long lastClickTime;

    /**
     * 构造
     *
     * @param context context
     */
    public ShelfSignInView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ShelfSignInView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_shelf_signin, this);
        textViewTime = view.findViewById(R.id.textview_time);
        buttonSignIn = view.findViewById(R.id.button_signin);
        textViewMinute = view.findViewById(R.id.textview_minute);
        textViewReadTips = view.findViewById(R.id.textview_read_tips);
        TypefaceUtils.setHwChineseMediumFonts(buttonSignIn);
        progressBar = view.findViewById(R.id.progress_readtime);
    }

    private void initData() {
        setReadTimeData(SpUtil.getinstance(getContext()).getAccountLoginStatus());
        boolean isLogin = SpUtil.getinstance(getContext()).getAccountLoginStatus();
        if (isLogin) {
            buttonSignIn.setText(getResources().getString(R.string.str_jrqd));
        } else {
            buttonSignIn.setText(getResources().getString(R.string.str_qqd));
            ifNotLoginSetMsg();
        }
    }


    private void setListener() {
        buttonSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > TempletContant.CLICK_DISTANSE) {
                    ActionEngine.getInstance().toSign((Activity) mContext);
                }
                lastClickTime = currentClickTime;
            }
        });
    }

    /**
     * 设置阅读时长
     *
     * @param isLogin isLogin
     */
    public void setReadTimeData(boolean isLogin) {
        long maxTime = 500 * 60 * 1000;
        try {
            SpUtil spUtil = SpUtil.getinstance(mContext);
            long readTime = spUtil.getLong(SpUtil.SP_READING_TIME, 0);
            long readTimeMin = readTime / 60000;
            long maxReadTime = spUtil.getLong(SpUtil.SP_MAX_READING_TIME, maxTime);
            if (maxReadTime == 0) {
                maxReadTime = maxTime;
            }
            progressBar.setProgress((int) (readTime * 100 / maxReadTime));
            if (isLogin) {
                textViewReadTips.setText(getResources().getString(R.string.this_week_has_been_read));
                setTimeViewVisible(VISIBLE);
                textViewTime.setText(String.valueOf(readTimeMin));
            } else {
                ifNotLoginSetMsg();
            }
        } catch (Exception ex) {
            progressBar.setProgress((int) (0 / maxTime));
            if (isLogin) {
                textViewReadTips.setText(getResources().getString(R.string.this_week_has_been_read));
                setTimeViewVisible(VISIBLE);
                textViewTime.setText("0");
            } else {
                ifNotLoginSetMsg();
            }
        }
    }

    private void ifNotLoginSetMsg() {
        textViewReadTips.setText(getResources().getString(R.string.str_qdlkd));
        setTimeViewVisible(GONE);
    }

    private void setTimeViewVisible(int visible) {
        textViewTime.setVisibility(visible);
        textViewMinute.setVisibility(visible);
    }

    /**
     * 设置签到数据
     *
     * @param updateInfo updateInfo
     */
    public void bindSignInData(BeanBookUpdateInfo updateInfo) {
        boolean isLogin = SpUtil.getinstance(getContext()).getAccountLoginStatus();
        setReadTimeData(isLogin);
        if (!isLogin) {
            buttonSignIn.setText(getResources().getString(R.string.str_qqd));
            buttonSignIn.setSelected(false);
        } else if (updateInfo.isSignIn()) {
            buttonSignIn.setText(getResources().getString(R.string.str_jryq));
            buttonSignIn.setSelected(true);
        } else {
            buttonSignIn.setText(getResources().getString(R.string.str_jrqd));
            buttonSignIn.setSelected(false);
        }
    }

}
