package com.dzbook.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.GiftUI;
import com.dzbook.mvp.presenter.GiftPresenter;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.List;

import hw.sdk.net.bean.gift.GiftListBean;

/**
 * 礼品
 *
 * @author KongXP on 2018/4/20.
 */
public class GiftExchangeFragment extends BaseFragment implements View.OnClickListener, GiftUI {
    private TextView mTvExchange;
    private EditText mEdtGiftCode;
    private GiftPresenter mPresenter;
    private TextView mTvResult;

    @Override
    public String getTagName() {
        return "GiftExchangeFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gift_exchange, container, false);
    }

    @Override
    protected void initView(View uiView) {
        mTvExchange = uiView.findViewById(R.id.tv_exchange);
        mEdtGiftCode = uiView.findViewById(R.id.edt_gift_code);
        mTvResult = uiView.findViewById(R.id.tv_result);
        TypefaceUtils.setHwChineseMediumFonts(mTvExchange);
        mEdtGiftCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                if (s.length() >= 1) {
                    mTvExchange.setBackground(CompatUtils.getDrawable(mActivity, R.drawable.bg_rounded_red));
                } else {
                    mTvExchange.setBackground(CompatUtils.getDrawable(mActivity, R.drawable.bg_rounded_gift_btn));
                }
                mTvResult.setText("");
                mTvResult.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                if (s.length() >= 1) {
                    mTvExchange.setBackground(CompatUtils.getDrawable(mActivity, R.drawable.bg_rounded_red));
                } else {
                    mTvExchange.setBackground(CompatUtils.getDrawable(mActivity, R.drawable.bg_rounded_gift_btn));
                }
            }
        });
    }

    @Override
    protected void initData(View uiView) {
        if (mPresenter == null) {
            mPresenter = new GiftPresenter(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setListener(View uiView) {
        mTvExchange.setOnClickListener(this);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exchange:
                if (!NetworkUtils.getInstance().checkNet()) {
                    if (getActivity() instanceof BaseActivity) {
                        ((BaseActivity) getActivity()).showNotNetDialog();
                    }
                } else {
                    ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.GIFT_CENTER_EXCHANGE_UMENG_ID, ThirdPartyLog.GIFT_CENTER_EXCHANGE_VALUE, 1);
                    DzLog.getInstance().logClick(LogConstants.MODULE_LPZX, LogConstants.ZONE_LPZX_DH, "", null, null);
                    if (!mEdtGiftCode.getText().toString().isEmpty()) {
                        if (LoginUtils.getInstance().checkLoginStatus(getContext())) {
                            mPresenter.getGiftExchangeFromNet(mEdtGiftCode.getText().toString());
                        }
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void showNoNetView() {

    }

    @Override
    public void setRecordList(List<GiftListBean> list, boolean refresh) {

    }

    @Override
    public void showEmptyView() {

    }

    @Override
    public void stopLoadMore() {

    }

    @Override
    public void setHasMore(boolean hasMore) {

    }

    @Override
    public void showLoadProgress() {

    }

    @Override
    public void dismissLoadProgress() {

    }

    @Override
    public void setResultMsg(String pResult) {
        mTvResult.setVisibility(View.VISIBLE);
        mTvResult.setText(pResult);
    }

    @Override
    public void showAllTips() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }
}
