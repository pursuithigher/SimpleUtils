package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.Intent;

import com.dzbook.activity.GuideActivity;
import com.dzbook.activity.Main2Activity;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.UI.GuideUI;
import com.dzbook.service.HwIntentService;
import com.dzbook.utils.SpUtil;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

/**
 * GuidePresenterImpl
 *
 * @author dongdianzhou on 2017/8/22.
 */

public class GuidePresenterImpl extends BasePresenter {

    private GuideUI mUI;

    /**
     * 构造
     *
     * @param guideUI guideUI
     */
    public GuidePresenterImpl(GuideUI guideUI) {
        mUI = guideUI;
    }


    /**
     * 完成指引
     */
    public void finishGuide() {
        finishGuideByType();
    }

    private void finishGuideByType() {
        if (SDCardUtil.getInstance().isSDCardAvailable()) {
            // 去主页面。
            Intent intent = new Intent(mUI.getContext(), Main2Activity.class);
            if (!SpUtil.getinstance(mUI.getContext()).getBoolean(SpUtil.IS_APP_INITIALIZED, false)) {
                startBuidDataService();
                SpUtil spUtil = SpUtil.getinstance(mUI.getContext());
                if (spUtil != null) {
                    spUtil.setBoolean(SpUtil.IS_APP_INITIALIZED, true);
                    spUtil.setBoolean(SpUtil.HW_IS_SHOW_GUIDE, true);
                }

                intent.putExtra("from", GuideActivity.class.getName());
            }
            mUI.getContext().startActivity(intent);
            ((Activity) mUI.getContext()).finish();
            //            ((Activity)mUI.getContext()).overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
        } else {
            ToastAlone.showShort(R.string.toast_sdcard_no_exist);
            ((Activity) mUI.getContext()).finish();
        }
    }


    private void startBuidDataService() {
        Intent intent = new Intent(mUI.getContext(), HwIntentService.class);
        intent.putExtra(HwIntentService.SERVICE_TYPE, HwIntentService.BUID_IN_BOOK_DATA);
        mUI.getContext().startService(intent);
    }
}
