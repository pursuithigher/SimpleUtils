package com.dzbook.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.activity.hw.PrivacyActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.net.hw.RequestCall;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.PackageControlUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.UtilTest;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.person.PersonCommonView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import hw.sdk.utils.UiHelper;

/**
 * 书架-侧滑菜单-关于我们activity
 *
 * @author dllik 2013-11-23
 */
public class AboutActivity extends BaseSwipeBackActivity implements OnClickListener {

    private static final String TAG = "AboutActivity";
    /**
     * 点击最长间隔
     */
    private static final int CLICK_NUM = 3;
    private static final int CLICK_DIV = 300;

    int clickCount = 0;
    /**
     * 上次点击的时间
     */
    int lastClickId = 0;
    long clickTime = 0;


    /**
     * 关于的应用icon
     */
    private ImageView mImgAboutIcon;
    /**
     * 关于左侧的button，用于测试功能。
     */
    private ImageView mImgTest;


    private DianZhongCommonTitle mCommonTitle;
    private PersonCommonView mTextViewPhoneNumContent;
    private TextView mTvVersion;
    private TextView mTvNotice;
    private TextView mTvStatement;
    private TextView mTvStatement2;
    private TextView mTvAnd;
    private Button tvOpenSource;
    private TextView mTvAppNameAbout;
    private TextView mReservedView1;
    private TextView mReservedView2;
    private long lastLongClickTime = 0;

    /**
     * 启动
     *
     * @param activity 启动依赖activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, AboutActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_about1);
    }

    /**
     * 初始化数据
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        try {
            mTvVersion.setText(getString(R.string.str_version_number) + PackageControlUtils.getAppVersionName());
            mTextViewPhoneNumContent.setTextViewContent(getResources().getString(R.string.phone_num_content));
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvNotice.setOnClickListener(this);
        mTvStatement.setOnClickListener(this);
        mTvStatement2.setOnClickListener(this);
        mTextViewPhoneNumContent.setOnClickListener(this);
        mImgAboutIcon.setOnClickListener(this);
        mImgTest.setOnClickListener(this);
        tvOpenSource.setOnClickListener(this);
    }

    /**
     * 初始化V控件
     */
    @Override
    protected void initView() {
        mCommonTitle = findViewById(R.id.commontitle);
        mTvNotice = findViewById(R.id.tv_notice);
        mTvStatement = findViewById(R.id.tv_statement);
        mTvVersion = findViewById(R.id.tv_version);
        mTextViewPhoneNumContent = findViewById(R.id.tv_phone_num_content);
        mImgAboutIcon = findViewById(R.id.img_about_icon);
        mImgTest = findViewById(R.id.img_test);
        mTvAnd = findViewById(R.id.tv_and);
        mReservedView1 = findViewById(R.id.rights_reserved1);
        mReservedView2 = findViewById(R.id.rights_reserved2);
        mTvStatement2 = findViewById(R.id.tv_statement2);
        mTvAppNameAbout = findViewById(R.id.tv_app_name_about);
        tvOpenSource = findViewById(R.id.tvOpenSource);
        TypefaceUtils.setHwChineseMediumFonts(mTvAppNameAbout);

        TypefaceUtils.setHwChineseMediumFonts(tvOpenSource);
        TypefaceUtils.setHwChineseMediumFonts(mTvNotice);
        TypefaceUtils.setHwChineseMediumFonts(mTvStatement);
        TypefaceUtils.setHwChineseMediumFonts(mTvStatement2);
        TypefaceUtils.setRegularFonts(mTvAnd);
        TypefaceUtils.setRegularFonts(mReservedView1);
        TypefaceUtils.setRegularFonts(mReservedView2);

        if (DeviceUtils.isZh(AboutActivity.this)) {
            mTvStatement.setVisibility(View.VISIBLE);
            mTvStatement2.setVisibility(View.GONE);
        } else {
            mTvAnd.setText(String.format(" %s ", mTvAnd.getText().toString()));
            mTvStatement.setVisibility(View.GONE);
            mTvStatement2.setVisibility(View.VISIBLE);
        }
        //修改按钮宽度为屏幕50%
        int screenWidth = UiHelper.getScreenWidth(this);
        ViewGroup.LayoutParams params = tvOpenSource.getLayoutParams();
        params.width = (int) (screenWidth * 0.5);
        tvOpenSource.setLayoutParams(params);
    }

    /**
     * 添加onclick
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (v.getId()) {
            case R.id.tv_notice:
                clickTextLink(RequestCall.getUrlAgreement(), getResources().getString(R.string.hua_wei_reads_and_sends_notices));
                break;
            case R.id.tv_statement:
            case R.id.tv_statement2:
                clickTextLink(RequestCall.getUrlPrivacyPolicy(), getResources().getString(R.string.privacy_statement));
                break;
            case R.id.tv_phone_num_content:
                callPhone();
                break;
            case R.id.img_about_icon:
                clickAboutIcon(id);
                break;
            case R.id.img_test:
                clickImgTest(id);
                break;
            case R.id.tvOpenSource:
                PrivacyActivity.show(getActivity(), "file:///android_asset/openSource.html", getResources().getString(R.string.open_source));
                break;
            default:
                break;
        }
        lastClickId = id;
    }

    private void clickTextLink(String urlAgreement, String title) {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastLongClickTime > 2000) {
            if (NetworkUtils.getInstance().checkNet()) {
                PrivacyActivity.show(getActivity(), urlAgreement, title);
            } else {
                showNotNetDialog();
            }
        }
        lastLongClickTime = currentClickTime;
    }

    private void clickAboutIcon(int id) {
        if (!ALog.getDebugMode()) {
            return;
        }
        long t = System.currentTimeMillis();
        if (t - clickTime > CLICK_DIV || id != lastClickId) {
            clickCount = 0;
        }
        // 标记本次点击时间
        clickTime = t;

        if (++clickCount >= CLICK_NUM) {
            clickCount = 0;
            new UtilTest().showSoftInfo(AboutActivity.this);
        }
    }

    private void clickImgTest(int id) {
        if (!ALog.getDebugMode()) {
            return;
        }
        long tt = System.currentTimeMillis();
        if (tt - clickTime > CLICK_DIV || id != lastClickId) {
            clickCount = 0;
        }

        // 标记本次点击时间
        clickTime = tt;

        if (++clickCount >= CLICK_NUM) {
            clickCount = 0;
            new UtilTest().showSetIp(AboutActivity.this);
        }
    }

    @SuppressLint("MissingPermission")
    private void callPhone() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.str_tel) + getResources().getString(R.string.phone_num_content)));
            startActivity(intent);
        } catch (Throwable e) {
            ALog.printExceptionWz(e);
        }
    }

    @Override
    protected void onResume() {
        clickCount = 0;
        clickTime = System.currentTimeMillis();
        super.onResume();
    }

    @Override
    public boolean needCheckPermission() {
        return false;
    }

}
