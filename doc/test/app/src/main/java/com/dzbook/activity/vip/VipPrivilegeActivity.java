package com.dzbook.activity.vip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import hw.sdk.utils.UiHelper;

/**
 * vip特权页
 *
 * @author gavin
 */
public class VipPrivilegeActivity extends BaseSwipeBackActivity {
    private String tag = "VipPrivilegeActivity";
    private DianZhongCommonTitle mCommonTitle;

    /**
     * 处理跳转
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, VipPrivilegeActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_privilege);
    }

    @Override
    public String getTagName() {
        return tag;
    }

    @Override
    protected void initView() {
        super.initView();
        mCommonTitle = findViewById(R.id.commontitle);
        TextView help = findViewById(R.id.tv_pri_help);
        TextView free = findViewById(R.id.tv_free);
        TextView activity = findViewById(R.id.tv_activity);
        TextView discount = findViewById(R.id.tv_discount);
        TextView voucher = findViewById(R.id.tv_voucher);
        TextView retroactive = findViewById(R.id.tv_retroactive);
        TextView mark = findViewById(R.id.tv_mark);
        TypefaceUtils.setHwChineseMediumFonts(help);
        TypefaceUtils.setHwChineseMediumFonts(free);
        TypefaceUtils.setHwChineseMediumFonts(activity);
        TypefaceUtils.setHwChineseMediumFonts(discount);
        TypefaceUtils.setHwChineseMediumFonts(voucher);
        TypefaceUtils.setHwChineseMediumFonts(retroactive);
        TypefaceUtils.setHwChineseMediumFonts(mark);
        //顶部图片比例
        ImageView imageView = findViewById(R.id.iv_vip_top);
        int screenWidth = UiHelper.getScreenWidth(this);
        int imageHeight = screenWidth * 128 / 360;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, imageHeight);
        imageView.setLayoutParams(params);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
