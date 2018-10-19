package com.dzbook.view.person;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.activity.person.PersonAccountActivity;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.PersonCenterPresenter;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.utils.hw.WalletManagerUtils;
import com.dzbook.view.SelectableRoundedImageView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.text.DecimalFormat;

/**
 * PersonTopView
 *
 * @author dongdianzhou on 2017/4/5.
 */

public class PersonTopView extends RelativeLayout implements View.OnClickListener {
    private static final int MAX_CLICK_INTERVAL_TIME = 1000;
    private Context mContext;
    /**
     * 登录未登录双布局
     */
    private RelativeLayout mRlLogin;
    private RelativeLayout mRlUnLogin;
    /**
     * 头像
     */
    private SelectableRoundedImageView mCircleImageViewPhoto;
    /**
     * 用户名
     */
    private TextView mTvUserNickNameOrId;
    /**
     * vip框
     */
    private LinearLayout mLlVip;
    /**
     * vip图标
     */
    private ImageView mIvVipLogo;
    /**
     * vip图标
     */
    private TextView mIvVipTxt;
    /**
     * 看点个数
     */
    private TextView mTvRemain;
    /**
     * 代金券个数
     */
    private TextView mTvVouchers;
    /**
     * 花币个数
     */
    private TextView mTvHb;
    /**
     * 看点LL
     */
    private LinearLayout mLlSeeCoin;
    /**
     * 代金券LL
     */
    private LinearLayout mLlVouchers;
    /**
     * 花币LL
     */
    private LinearLayout mLinearHCoin;

    private long lastClickTime = 0;


    private PersonCenterPresenter mPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public PersonTopView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PersonTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    public void setPresenter(PersonCenterPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }

    private void setListener() {
        mCircleImageViewPhoto.setOnClickListener(this);
        mRlUnLogin.setOnClickListener(this);
        mLinearHCoin.setOnClickListener(this);
        mLlSeeCoin.setOnClickListener(this);
        mLlVouchers.setOnClickListener(this);
        mTvUserNickNameOrId.setOnClickListener(this);
        mLlVip.setOnClickListener(this);
    }

    private void initData() {
        mTvHb.setText("--");
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_person_top, this);
        mRlLogin = view.findViewById(R.id.rl_login);
        mRlUnLogin = view.findViewById(R.id.rl_unlogin);
        mCircleImageViewPhoto = view.findViewById(R.id.circleview_photo);
        mTvUserNickNameOrId = view.findViewById(R.id.tv_user_nickname_or_id);
        mLlVip = view.findViewById(R.id.ll_vip);
        mIvVipLogo = view.findViewById(R.id.iv_vip_logo);
        mIvVipTxt = view.findViewById(R.id.iv_vip_txt);
        mTvRemain = view.findViewById(R.id.tv_remain);
        mTvVouchers = view.findViewById(R.id.tv_vouchers);
        mTvHb = view.findViewById(R.id.tv_hb);
        mLinearHCoin = view.findViewById(R.id.linear_hcoin);
        mLlSeeCoin = view.findViewById(R.id.ll_see_coin);
        mLlVouchers = view.findViewById(R.id.ll_vouchers);
    }

    @Override
    public void onClick(View v) {

        long thisClickTime = System.currentTimeMillis();
        if (Math.abs(thisClickTime - lastClickTime) < MAX_CLICK_INTERVAL_TIME) {
            return;
        }
        lastClickTime = thisClickTime;

        if (NetworkUtils.NETWORK_NONE == NetworkUtils.getInstance().getNetworkState()) {
            mPresenter.showNotNetDialog();
        } else {
            switch (v.getId()) {
                case R.id.circleview_photo:
                    DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_TX, "", null, null);
                    mPresenter.intentToHwAccountCenter();
                    break;
                case R.id.tv_user_nickname_or_id:
                    DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_TX, "", null, null);
                    mPresenter.intentToHwAccountCenter();
                    break;
                //我的包月vip页面
                case R.id.ll_vip:
                    DzLog.getInstance().logClick(LogConstants.MODULE_WD, LogConstants.ZONE_WD_WDVIP, "", null, null);
                    mPresenter.intentToMyVipActivity();
                    break;
                //登录
                case R.id.rl_unlogin:
                    mPresenter.login();
                    break;
                case R.id.linear_hcoin:
                    WalletManagerUtils.startHcoinActivity((Activity) mContext);
                    break;
                case R.id.ll_see_coin:
                    if (!NetworkUtils.getInstance().checkNet()) {
                        if (mContext instanceof BaseActivity) {
                            ((BaseActivity) mContext).showNotNetDialog();
                        }
                    } else {
                        LoginUtils.getInstance().forceLoginCheck(mContext, new LoginUtils.LoginCheckListener() {
                            @Override
                            public void loginComplete() {
                                PersonAccountActivity.launch((Activity) mContext);
                            }
                        });
                    }
                    break;
                case R.id.ll_vouchers:
                    if (!NetworkUtils.getInstance().checkNet()) {
                        if (mContext instanceof BaseActivity) {
                            ((BaseActivity) mContext).showNotNetDialog();
                        }
                    } else {
                        LoginUtils.getInstance().forceLoginCheck(mContext, new LoginUtils.LoginCheckListener() {
                            @Override
                            public void loginComplete() {
                                PersonAccountActivity.launch((Activity) mContext);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置view
     *
     * @param isReferenceUserInfo isReferenceUserInfo
     */
    public void referenceView(boolean isReferenceUserInfo) {

        initUserInfo(isReferenceUserInfo);
    }

    @SuppressLint("SetTextI18n")
    private void initUserInfo(boolean isReferenceUserInfo) {
        if (isReferenceUserInfo) {
            SpUtil spf = SpUtil.getinstance(mContext);
            if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                String name = spf.getLoginUserNickNameByUserId();
                mRlUnLogin.setVisibility(View.GONE);
                mRlLogin.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(spf.getLoginUserCoverWapByUserId()).into(mCircleImageViewPhoto);
                mTvRemain.setText(spf.getUserRemainPrice());
                mTvVouchers.setText(spf.getUserVouchers());
                if (spf.getInt(SpUtil.DZ_IS_VIP) == 1) {
                    mIvVipTxt.setText(getResources().getString(R.string.person_top_vip));
                    mTvUserNickNameOrId.setVisibility(View.GONE);
                    mIvVipLogo.setVisibility(View.VISIBLE);
                    mTvUserNickNameOrId.setVisibility(View.VISIBLE);
                } else {
                    mIvVipTxt.setText(getResources().getString(R.string.person_top_vip_not));
                    mTvUserNickNameOrId.setVisibility(View.GONE);
                    mIvVipLogo.setVisibility(View.GONE);
                    mTvUserNickNameOrId.setVisibility(View.VISIBLE);
                }
                mTvUserNickNameOrId.setText(name);
            } else {
                mRlUnLogin.setVisibility(View.VISIBLE);
                mRlLogin.setVisibility(View.GONE);
            }
            getHCoin();
        }
    }

    /**
     * 得到华为花币数量
     */
    private void getHCoin() {
        if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
            WalletManagerUtils.queryWalletInfo((Activity) mContext, new WalletManagerUtils.HcoinQueryCallBack() {
                @SuppressLint("SetTextI18n")
                @Override
                public void querySuccess(float hCoin) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    mTvHb.setText(df.format(hCoin));
                }
            });
        }
    }
}
