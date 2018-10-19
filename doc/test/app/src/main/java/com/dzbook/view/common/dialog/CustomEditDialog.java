package com.dzbook.view.common.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;

/**
 * Dialog： 编辑框模式
 */
public class CustomEditDialog extends CustomDialogBusiness {

    private EditText editText;

    /**
     * 构造
     *
     * @param context context
     */
    public CustomEditDialog(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param style   style
     */
    public CustomEditDialog(Context context, int style) {
        super(context, style);
    }

    @Override
    protected View getView() {
        editText = new EditText(context);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setMinHeight(DimensionPixelUtil.dip2px(context, 48));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        editText.setTextColor(Color.parseColor("#000000"));
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftInput();
            }
        });
        return editText;
    }

    @Override
    protected Object getConfirmEvent() {
        hideSoftInput();
        return editText.getText().toString();
    }

    @Override
    protected void getCancelEvent() {
        hideSoftInput();
    }

    private void hideSoftInput() {
        Window window = customDialog.getWindow();
        if (window == null) {
            return;
        }
        window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); //强制隐藏键盘
        }
    }

    private void showSoftInput() {
        Window window = customDialog.getWindow();
        if (window == null) {
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, 0);
        }
    }
}