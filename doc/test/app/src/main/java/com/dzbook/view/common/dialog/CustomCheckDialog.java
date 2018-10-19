package com.dzbook.view.common.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.common.CustomCheckBox;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;

/**
 * Dialog： 单选模式
 *
 * @author wangjianchen
 */
public class CustomCheckDialog extends CustomDialogBusiness {

    private CustomCheckBox checkBox;

    /**
     * 构造
     *
     * @param context context
     */
    public CustomCheckDialog(Context context) {
        super(context);
    }

    @Override
    protected View getView() {
        checkBox = new CustomCheckBox(context);
        checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(context, 48)));
        return checkBox;
    }

    @Override
    protected Object getConfirmEvent() {
        return checkBox.isSelected();
    }

    @Override
    protected void getCancelEvent() {

    }

    /**
     * 文字描述
     *
     * @param desc desc
     */
    public void setDesc(String desc) {
        checkBox.setDesc(desc);
    }
}