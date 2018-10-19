package com.dzbook.activity.person;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.database.bean.PluginInfo;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.PersonPluginUI;
import com.dzbook.mvp.presenter.PersonPluginPresenter;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PluginDLProgress;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;

/**
 * 插件管理
 *
 * @author wxliao on 18/1/16.
 */
public class PersonPluginActivity extends BaseSwipeBackActivity implements PersonPluginUI, View.OnClickListener {
    private static final String TAG = "PersonPluginActivity";

    private DianZhongCommonTitle mCommonTitle;
    private RelativeLayout mLayoutTts;
    private TextView mTvTts;
    private PluginDLProgress mProgressbarTts;
    private RelativeLayout mLayoutWps;
    private TextView mTvWps;
    private PluginDLProgress mProgressbarWps;
    private PersonPluginPresenter mPresenter;
    private RelativeLayout mRlTts;
    private RelativeLayout mRlWps;
    private StatusView statusView;

    /**
     * 启动
     *
     * @param context context
     */
    public static void launch(Context context) {
        Intent intent = new Intent(context, PersonPluginActivity.class);
        context.startActivity(intent);
        showActivity(context);
    }

    /**
     * 启动
     *
     * @param context         context
     * @param toInstallPlugin toInstallPlugin
     */
    public static void launch(Context context, String toInstallPlugin) {
        Intent intent = new Intent(context, PersonPluginActivity.class);
        intent.putExtra("toInstallPlugin", toInstallPlugin);
        context.startActivity(intent);
        showActivity(context);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_plugin);
    }

    @Override
    protected void initView() {
        super.initView();
        statusView = findViewById(R.id.statusView);
        mCommonTitle = findViewById(R.id.commontitle);

        mLayoutTts = findViewById(R.id.layout_tts);
        mTvTts = findViewById(R.id.textView_tts);
        mProgressbarTts = findViewById(R.id.progressBar_tts);

        mLayoutWps = findViewById(R.id.layout_wps);
        mTvWps = findViewById(R.id.textView_wps);
        mProgressbarWps = findViewById(R.id.progressBar_wps);

        mRlTts = findViewById(R.id.rl_tts_bg);
        mRlWps = findViewById(R.id.rl_wps_bg);
        mTvTts.setSelected(true);
        TypefaceUtils.setHwChineseMediumFonts(mTvTts);
        TypefaceUtils.setHwChineseMediumFonts(mTvWps);
    }

    @Override
    protected void initData() {
        super.initData();
        String toInstallPlugin = null;
        Intent intent = getIntent();
        if (intent != null) {
            toInstallPlugin = intent.getStringExtra("toInstallPlugin");
        }
        mPresenter = new PersonPluginPresenter(this);
        if (NetworkUtils.NETWORK_NONE == NetworkUtils.getInstance().getNetworkState()) {
            showNotNetDialog();
        } else {
            if (null != toInstallPlugin && PluginInfo.TTS_NAME.equals(toInstallPlugin)) {
                if (NetworkUtils.NETWORK_WIFI != NetworkUtils.getInstance().getNetworkState()) {
                    notWifiDialog(toInstallPlugin);
                } else {
                    mPresenter.getData(toInstallPlugin);
                }
            } else {
                mPresenter.getData(toInstallPlugin);
            }
        }
    }


    @Override
    protected void setListener() {
        super.setListener();
        mTvTts.setOnClickListener(this);
        mTvWps.setOnClickListener(this);
        mLayoutTts.setOnClickListener(this);
        mLayoutWps.setOnClickListener(this);
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                mPresenter.getData("");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_tts:
                doTts();
                break;
            case R.id.textView_wps:
                doWps();
                break;
            case R.id.layout_tts:
                doTts();
                break;
            case R.id.layout_wps:
                doWps();
                break;
            default:
                break;
        }
    }

    private void doTts() {
        String text = mTvTts.getText().toString();
        if (TextUtils.equals(text, mPresenter.textFreeLoad)
                || TextUtils.equals(text, mPresenter.textUpdate)) {
            mPresenter.showNotWifiDialog(0);
        }
    }

    private void doWps() {
        String text = mTvWps.getText().toString();
        if (TextUtils.equals(text, mPresenter.textFreeLoad)
                || TextUtils.equals(text, mPresenter.textUpdate)) {
            mPresenter.showNotWifiDialog(1);
        }
        if (TextUtils.equals(text, mPresenter.textInstall)) {
            mPresenter.isDownLoadEd(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.refreshWpsInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @SuppressLint({"SetTextI18n", "StringFormatInvalid"})
    @Override
    public void showTtsItem(int progress, String text) {
        mLayoutTts.setVisibility(View.VISIBLE);
        mTvTts.setText(text);
        mProgressbarTts.setProgress(progress);
        handleBigTouch(text, mTvTts, mProgressbarTts, mRlTts);

        if (TextUtils.equals(text, mPresenter.textFreeLoad)) {
            mTvTts.setSelected(false);
            mTvTts.setEnabled(true);
        } else if (TextUtils.equals(text, mPresenter.textInstalled) || TextUtils.equals(text, mPresenter.textDisable)) {
            mTvTts.setSelected(false);
            mTvTts.setEnabled(false);
        } else {
            mTvTts.setSelected(true);
            mTvTts.setEnabled(true);
        }
        mProgressbarTts.setProgress(progress);
    }

    private void handleBigTouch(String text, TextView textView, PluginDLProgress progress, RelativeLayout layout) {
        if (mPresenter.textFreeLoad.equals(text)) {
            showBigTouch(textView, progress, true, layout);
        } else if (mPresenter.textUpdate.equals(text)) {
            showBigTouch(textView, progress, true, layout);
        } else if (mPresenter.textLoading.equals(text)) {
            showTouch(textView, progress, layout);
        } else if (mPresenter.textInstall.equals(text)) {
            showBigTouch(textView, progress, true, layout);
        } else if (mPresenter.textInstalled.equals(text)) {
            showBigTouch(textView, progress, false, layout);
        } else if (mPresenter.textDisable.equals(text)) {
            showBigTouch(textView, progress, false, layout);
        }
    }

    @SuppressLint({"SetTextI18n", "StringFormatInvalid"})
    @Override
    public void showWpsItem(int progress, String text) {
        mLayoutWps.setVisibility(View.VISIBLE);
        mTvWps.setText(text);
        handleBigTouch(text, mTvWps, mProgressbarWps, mRlWps);
        if (TextUtils.equals(text, mPresenter.textFreeLoad)) {
            mTvWps.setSelected(false);
            mTvWps.setEnabled(true);
        } else if (TextUtils.equals(text, mPresenter.textInstall)) {
            mTvWps.setSelected(false);
            mTvWps.setEnabled(true);
        } else if (TextUtils.equals(text, mPresenter.textInstalled) || TextUtils.equals(text, mPresenter.textDisable)) {
            mTvWps.setSelected(false);
            mTvWps.setEnabled(false);
        } else {
            mTvWps.setSelected(true);
            mTvWps.setEnabled(true);
        }
        mProgressbarWps.setProgress(progress);
    }

    @Override
    public void setStatusViewType(int pType) {
        switch (pType) {
            case 0:
                statusView.showSuccess();
                break;
            case 1:
                statusView.showLoading();
                break;
            case 2:
                statusView.showNetError();
                break;
            default:
                break;
        }
    }

    private void setTrueTouchBg(TextView v, RelativeLayout pRlBg) {
        pRlBg.setBackgroundResource(R.drawable.selector_hw_btn_common1);
        v.setBackgroundResource(R.color.transparent);
        v.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_000000));
        v.setGravity(Gravity.CENTER);
    }


    @SuppressLint("RtlHardcoded")
    private void setFalseTouchBg(TextView v, RelativeLayout pRlBg) {
        pRlBg.setBackgroundResource(R.color.transparent);
        v.setBackgroundResource(R.color.transparent);
        v.setTextColor(CompatUtils.getColor(getContext(), R.color.color_50_1A1A1A));
        v.setGravity(Gravity.RIGHT | Gravity.CENTER);
    }

    private void showTouch(TextView pTvBig, PluginDLProgress pPro, RelativeLayout pRlBg) {
        pRlBg.setBackground(CompatUtils.getDrawable(getContext(), R.drawable.plugin_progress_rl_bg));
        pTvBig.setVisibility(View.GONE);
        pPro.setVisibility(View.VISIBLE);
    }

    private void showBigTouch(TextView pTvBig, PluginDLProgress pPro, Boolean isTouch, RelativeLayout pRlBg) {
        pTvBig.setVisibility(View.VISIBLE);
        pPro.setVisibility(View.GONE);
        if (isTouch) {
            setTrueTouchBg(pTvBig, pRlBg);
        } else {
            TypefaceUtils.setRegularFonts(pTvBig);
            setFalseTouchBg(pTvBig, pRlBg);
        }
    }

    /**
     * 移动网络，dialog提示用户消耗流量
     *
     * @param toInstallPlugin toInstallPlugin
     */
    public void notWifiDialog(final String toInstallPlugin) {
        CustomHintDialog customHintDialog = new CustomHintDialog(PersonPluginActivity.this, 1);
        customHintDialog.setDesc(getResources().getString(R.string.str_not_wifi_prompt));
        customHintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                mPresenter.getData(toInstallPlugin);
            }

            @Override
            public void clickCancel() {
                finish();
            }
        });
        customHintDialog.setConfirmTxt(getResources().getString(R.string.down_ok));
        customHintDialog.show();
    }
}
