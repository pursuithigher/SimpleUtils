package com.dzbook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.mvp.UI.GuideUI;
import com.dzbook.mvp.presenter.GuidePresenterImpl;
import com.dzbook.view.DianzhongDefaultLastTipView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

/**
 * 引导页
 *
 * @author dongdianzhou on 2017/8/22.
 */
public class GuideActivity extends BaseSwipeBackActivity implements GuideUI {

    private static final String TAG = "GuideActivity";

    private DianzhongDefaultLastTipView mGuideView;

    /**
     * 打开引导页
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, GuideActivity.class));
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_guide);
        setSwipeBackEnable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void initView() {
        super.initView();
        mGuideView = findViewById(R.id.guideview);
    }

    @Override
    protected void initData() {
        super.initData();
        GuidePresenterImpl mPresenter = new GuidePresenterImpl(this);
        mGuideView.setPresenter(mPresenter);
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }


}
