package com.dzbook.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.common.dialog.base.CustomDialogParent;
import com.ishugui.R;

import static hw.sdk.utils.UiHelper.getScreenWidth;

/**
 * vip页连续包月提示
 *
 * @author gavin
 */
public class VipTipsDialog extends CustomDialogParent {

    /**
     * 构造
     *
     * @param context context
     */
    public VipTipsDialog(@NonNull Context context) {
        super(context, R.style.cmt_dialog);
        setContentView(R.layout.dialog_vip_tips);
        Window window = getWindow();
        android.view.WindowManager.LayoutParams p = window.getAttributes();
        p.width = getScreenWidth(context);
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        TextView tv = findViewById(R.id.tv_vip_cancel);
        TypefaceUtils.setHwChineseMediumFonts(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void initData() {

    }

    private void setListener() {

    }
}
