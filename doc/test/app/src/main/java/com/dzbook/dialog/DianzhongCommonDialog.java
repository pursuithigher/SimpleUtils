package com.dzbook.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dzbook.view.common.dialog.base.CustomDialogParent;
import com.ishugui.R;

/**
 * 公共弹窗：
 * 1.需要登录弹窗
 * 2.清除缓存
 * 3.退出应用
 *
 * @author lizhongzhong 2016-11-18
 */
public class DianzhongCommonDialog extends CustomDialogParent implements View.OnClickListener {
    /**
     * 联系客服
     */
    private Button buttonLeft, buttonRight;
    private TextView textviewShowCenterTips;
    private DialogClickAction clickAction;

    /**
     * 构造
     *
     * @param activity activity
     */
    public DianzhongCommonDialog(Context activity) {
        super(activity, R.style.dialog_normal);
        setContentView(R.layout.dialog_common_with_button);
        setProperty();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        buttonRight = this.findViewById(R.id.button_right);
        buttonLeft = this.findViewById(R.id.button_left);
        textviewShowCenterTips = findViewById(R.id.textview_show_center_tips);
    }

    private void initData() {
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);

    }

    private void setListener() {
        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        dismiss();
        if (id == R.id.button_right) {
            if (clickAction != null) {
                clickAction.okAction();
            }
        } else if (id == R.id.button_left) {
            if (clickAction != null) {
                clickAction.cancelAction();
            }
        }
    }

    /**
     * 点击接口
     */
    public interface DialogClickAction {
        /**
         * 确认
         */
        void okAction();

        /**
         * 取消
         */
        void cancelAction();
    }


    /**
     * 显示
     *
     * @param content      content
     * @param okbutton     okbutton
     * @param cancelButton cancelButton
     * @param action       action
     */
    public void show(String content, String okbutton, String cancelButton, DialogClickAction action) {
        if (!TextUtils.isEmpty(content)) {
            textviewShowCenterTips.setText(content);
        }
        if (!TextUtils.isEmpty(okbutton)) {
            buttonRight.setText(okbutton);
        }
        if (!TextUtils.isEmpty(cancelButton)) {
            buttonLeft.setText(cancelButton);
        }
        clickAction = action;
        show();
    }

}
