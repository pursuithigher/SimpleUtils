package com.dzbook.view.vip;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.presenter.MyVipPresenter;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.vip.VipUserInfoBean;
import hw.sdk.utils.UiHelper;


/**
 * vip顶部布局
 *
 * @author gavin
 */
public class TopVipView extends LinearLayout {
    private Context mContext;
    private ImageView mIvUserLogo;
    private TextView mTvOpen;
    private TextView mTvUserName;
    private TextView mTvVipEndTime;
    private MyVipPresenter presenter;

    /**
     * 构造
     *
     * @param context   context
     * @param presenter presenter
     */
    public TopVipView(Context context, MyVipPresenter presenter) {
        this(context, null, presenter);
    }

    /**
     * 构造
     *
     * @param context   context
     * @param attrs     attrs
     * @param presenter presenter
     */
    public TopVipView(Context context, AttributeSet attrs, MyVipPresenter presenter) {
        super(context, attrs);
        mContext = context;
        this.presenter = presenter;
        initView();
        initData();
        setListener();

    }

    private void setListener() {
        mTvOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getVipWell();
            }
        });
    }

    private void initData() {

    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_vip_top, this);
        setOrientation(VERTICAL);
        mIvUserLogo = view.findViewById(R.id.iv_vip_user);
        mTvOpen = view.findViewById(R.id.tv_vip_open);
        mTvUserName = view.findViewById(R.id.tv_vip_name);
        mTvVipEndTime = view.findViewById(R.id.tv_vip_date);
        TextView mTvTitle = view.findViewById(R.id.tv_vip_packge_title);
        TypefaceUtils.setHwChineseMediumFonts(mTvUserName);
        TypefaceUtils.setHwChineseMediumFonts(mTvTitle);
        //顶部图片比例
        int screenWidth = UiHelper.getScreenWidth(mContext);
        ImageView mImageTop = findViewById(R.id.iv_vip_top);
        int imageHeight = screenWidth * 128 / 360;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, imageHeight);
        mImageTop.setLayoutParams(params);
    }

    /**
     * 绑定数据
     *
     * @param info info
     */
    public void bindData(VipUserInfoBean info) {
        if (info != null) {
            SpUtil spUtil = SpUtil.getinstance(mContext);

            if (!TextUtils.isEmpty(info.name)) {
                String name = spUtil.getLoginUserNickNameByUserId();
                if (!TextUtils.isEmpty(name)) {
                    mTvUserName.setText(name);
                } else {
                    mTvUserName.setText(info.name);
                }
            } else {
                mTvUserName.setText(getResources().getString(R.string.str_unlogin));
            }
            if (!TextUtils.isEmpty(info.deadLine)) {
                mTvVipEndTime.setText(info.deadLine);
            }
            if (info.isVip == 1) {
                mTvOpen.setVisibility(VISIBLE);
                if (info.isReceived == 0) {
                    mTvOpen.setText(getResources().getString(R.string.hw_vip_well));
                    mTvOpen.setTextColor(CompatUtils.getColor(getContext(), R.color.white));
                    mTvOpen.setBackgroundResource(R.drawable.shap_vip_open_bg);
                    mTvOpen.setEnabled(true);

                } else {
                    mTvOpen.setText(getResources().getString(R.string.already_received));
                    mTvOpen.setBackgroundResource(R.drawable.shap_well_bg);
                    mTvOpen.setTextColor(CompatUtils.getColor(getContext(), R.color.color_50_1A1A1A));
                    mTvOpen.setEnabled(false);
                }
            }
            String coverwap = spUtil.getLoginUserCoverWapByUserId();
            if (!TextUtils.isEmpty(coverwap)) {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), mIvUserLogo, coverwap, R.drawable.hw_person_top_avatar);
            } else {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(mContext, mIvUserLogo, info.userIcon, R.drawable.hw_avatar);
            }
        }
    }
}
