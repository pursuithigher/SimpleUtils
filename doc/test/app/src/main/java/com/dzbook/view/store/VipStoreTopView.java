package com.dzbook.view.store;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.MainTypeDetailActivity;
import com.dzbook.activity.vip.MyVipActivity;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;

import hw.sdk.net.bean.store.BeanVipInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * vip书城顶部布局
 */
public class VipStoreTopView extends ConstraintLayout implements View.OnClickListener {

    private Context mContext;

    private RelativeLayout relativeLayoutVipInfo;
    private TextView textViewOpenVip;
    private ImageView imageViewIcon;
    private TextView textViewName;
    private TextView textViewInfo;
    private AutoHeightImagView imageViewFl1;
    private AutoHeightImagView imageViewFl2;
    private long lastClickTime = 0;

    private TempletPresenter templetPresenter;

    /**
     * 构造
     *
     * @param context          context
     * @param templetPresenter templetPresenter
     */
    public VipStoreTopView(Context context, TempletPresenter templetPresenter) {
        super(context);
        this.templetPresenter = templetPresenter;
        mContext = context;
        initView();
        initData();
        setListener();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public VipStoreTopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        relativeLayoutVipInfo.setOnClickListener(this);
        imageViewFl1.setOnClickListener(this);
        imageViewFl2.setOnClickListener(this);

    }

    private void initData() {

    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_vipstore_top, this);
        relativeLayoutVipInfo = view.findViewById(R.id.relative_vipinfo);
        textViewOpenVip = view.findViewById(R.id.textview_vip_open);
        TypefaceUtils.setHwChineseMediumFonts(textViewOpenVip);
        imageViewIcon = view.findViewById(R.id.circleview_photo);
        textViewName = view.findViewById(R.id.textview_name);
        TypefaceUtils.setHwChineseMediumFonts(textViewName);
        textViewInfo = view.findViewById(R.id.textview_info);
        imageViewFl1 = view.findViewById(R.id.imageviewfl1);
        imageViewFl2 = view.findViewById(R.id.imageviewfl2);
        TypefaceUtils.setHwChineseMediumFonts((TextView) findViewById(R.id.textview_title));
    }

    /**
     * 绑定数据
     */
    public void bindData() {
        SpUtil spUtil = SpUtil.getinstance(mContext);
        int isVip = spUtil.getInt(SpUtil.DZ_IS_VIP);

        if (LoginUtils.getInstance().checkLoginStatus(getContext())) {
            if (isVip == 1) {
                textViewOpenVip.setVisibility(GONE);
                String vipTime = spUtil.getString(SpUtil.DZ_VIP_EXPIRED_TIME);
                textViewInfo.setText(String.format(getResources().getString(R.string.str_local_vip_time), vipTime));

            } else {
                textViewOpenVip.setVisibility(VISIBLE);
                textViewInfo.setText(getResources().getString(R.string.str_unopen_member));
            }
            String userName = spUtil.getLoginUserNickNameByUserId();
            textViewName.setText(userName);
            String coverwap = spUtil.getLoginUserCoverWapByUserId();
            if (!TextUtils.isEmpty(coverwap)) {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), imageViewIcon, coverwap, R.drawable.hw_person_top_avatar);
            }
        } else {
            textViewName.setText(getResources().getString(R.string.str_unlogin));
            textViewOpenVip.setVisibility(VISIBLE);
            textViewInfo.setText(getResources().getString(R.string.str_unopen_member));
            imageViewIcon.setImageResource(R.drawable.hw_person_top_avatar);
        }

    }

    /**
     * 绑定数据
     *
     * @param vipInfo vipInfo
     */
    public void bindData(BeanVipInfo vipInfo) {
        if (vipInfo != null) {
            if (!TextUtils.isEmpty(vipInfo.vipIcon)) {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), imageViewIcon, vipInfo.vipIcon, R.drawable.hw_person_top_avatar);
            }
            if (!TextUtils.isEmpty(vipInfo.vipName)) {
                String userName = SpUtil.getinstance(getContext()).getLoginUserNickNameByUserId();
                if (!TextUtils.isEmpty(userName)) {
                    textViewName.setText(userName);
                } else {
                    textViewName.setText(vipInfo.vipName);
                }

            } else {
                textViewName.setText(getResources().getString(R.string.str_unlogin));
            }
            textViewInfo.setText(vipInfo.vipTime);
            if (vipInfo.isVip == 1) {
                textViewOpenVip.setVisibility(GONE);
                String vipTime = SpUtil.getinstance(getContext()).getString(SpUtil.DZ_VIP_EXPIRED_TIME);
                textViewInfo.setText(String.format(getResources().getString(R.string.str_local_vip_time), vipTime));
            } else {
                textViewOpenVip.setVisibility(VISIBLE);
                textViewInfo.setText(getResources().getString(R.string.str_unopen_member));
            }
        }
    }


    @Override
    public void onClick(View v) {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > TempletContant.CLICK_DISTANSE) {
            int id = v.getId();
            if (id == R.id.relative_vipinfo) {
                MyVipActivity.launch((Activity) mContext);
                templetPresenter.logHwVip(false, 0);
                templetPresenter.logVip(0);
            } else if (id == R.id.imageviewfl2) {
                MainTypeDetailActivity.launchVip(mContext, getResources().getString(R.string.str_vip_title_type), "");
                templetPresenter.logHwVip(true, 2);
                templetPresenter.logVip(2);
            } else if (id == R.id.imageviewfl1) {
                MainTypeDetailActivity.launchVip(mContext, getResources().getString(R.string.str_vip_title_type), getResources().getString(R.string.str_vip_title_xh));
                templetPresenter.logHwVip(true, 1);
                templetPresenter.logVip(1);
            }
        }
    }
}
