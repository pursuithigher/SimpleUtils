package com.dzbook.view.common.dialog.base;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;

import com.dzbook.lib.utils.ALog;

/**
 * Dialog： 业务抽象层，业务层需要暴露的公共方法，和不同Style样式
 *
 * @author wangjianchen
 */
public abstract class CustomDialogBusiness {

    /**
     * 标准Style样式
     */
    public static final int STYLE_DIALOG_NORMAL = 1;

    /**
     * 居中样式
     */
    public static final int STYLE_DIALOG_CENTER = 2;

    /**
     * 只显示取消按钮
     */
    public static final int STYLE_DIALOG_CANCEL = 3;

    /**
     * 隐藏底部的确定取消
     */
    public static final int STYLE_DIALOG_BOTTOM_CANCEL = 4;

    protected Context context;
    protected CustomDialog customDialog;

    private boolean isCancel = true;
    private OnCheckListener checkListener;

    /**
     * 构造
     *
     * @param context context
     */
    protected CustomDialogBusiness(Context context) {
        this(context, STYLE_DIALOG_NORMAL);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param isCancel isCancel
     */
    protected CustomDialogBusiness(Context context, boolean isCancel) {
        this(context, STYLE_DIALOG_NORMAL, isCancel);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param style    style
     * @param isCancel isCancel
     */
    protected CustomDialogBusiness(Context context, int style, boolean isCancel) {
        this.isCancel = isCancel;
        this.context = context;
        initDialog(style);
    }

    /**
     * 构造
     *
     * @param context context
     * @param style   style
     */
    protected CustomDialogBusiness(Context context, int style) {
        this.context = context;
        initDialog(style);
    }

    /**
     * getView
     *
     * @return View
     */
    protected abstract View getView();

    /**
     * 确认事件
     *
     * @return Object
     */
    protected abstract Object getConfirmEvent();

    /**
     * 取消事件
     */
    protected abstract void getCancelEvent();

    /**
     * 标题
     *
     * @param title title
     */
    public void setTitle(String title) {
        customDialog.setTitle(title);
    }

    /**
     * 确认文本
     *
     * @param txt txt
     */
    public void setConfirmTxt(String txt) {
        customDialog.setConfirmTxt(txt);
    }

    /**
     * 取消文本
     *
     * @param txt txt
     */
    public void setCancelTxt(String txt) {
        customDialog.setCancelTxt(txt);
    }

    /**
     * 显示
     */
    public void show() {
        try {
            customDialog.show();
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        try {
            customDialog.dismiss();
        } catch (Exception e) {
            ALog.printExceptionWz(e);
        }
    }

    /**
     * 监听
     *
     * @param checkListener checkListener
     */
    public void setCheckListener(OnCheckListener checkListener) {
        this.checkListener = checkListener;
        customDialog.setBaseCheckListener(new CustomDialogBase.OnBaseCheckListener() {
            @Override
            public void baseClickConfirm() {
                clickConfirmEvent();
            }

            @Override
            public void baseClickCancel() {
                clickCancelEvent();
            }
        });
    }

    /**
     * 隐藏监听
     *
     * @param dismissListener dismissListener
     */
    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        if (dismissListener != null) {
            customDialog.setOnDismissListener(dismissListener);
        }
    }

    /**
     * 点击空白区域取消
     *
     * @param canceledOnTouchOutside cancel
     */
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        customDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    /**
     * 确定
     */
    public void clickConfirmEvent() {
        if (checkListener != null) {
            checkListener.clickConfirm(getConfirmEvent());
        }
        customDialog.dismiss();

    }

    /**
     * 取消
     */
    public void clickCancelEvent() {
        if (checkListener != null) {
            getCancelEvent();
            checkListener.clickCancel();
        }
        customDialog.dismiss();
    }

    private void initDialog(int style) {
        switch (style) {
            // 样式1
            case STYLE_DIALOG_NORMAL:
                customDialog = new CustomDialog.Builder(context).view(getView()).isCanTouchOut(isCancel).build();
                break;
            // 样式2 (居中)
            case STYLE_DIALOG_CENTER:
                customDialog = new CustomDialog.Builder(context).view(getView()).setGravity(Gravity.CENTER).build();
                break;
            //样式 3 （只有取消）
            case STYLE_DIALOG_CANCEL:
                customDialog = new CustomDialog.Builder(context).view(getView()).build();
                customDialog.hideConfim();
                break;
            case STYLE_DIALOG_BOTTOM_CANCEL:
                customDialog = new CustomDialog.Builder(context).view(getView()).build();
                customDialog.hideBottomLayout();
                break;
            default:
                break;
        }
    }

    /**
     * 接口
     */
    public interface OnCheckListener {
        /**
         * 点击确认
         *
         * @param object object
         */
        void clickConfirm(Object object);

        /**
         * 点击取肖
         */
        void clickCancel();
    }

    /**
     * 当前是否显示
     *
     * @return boolean
     */
    public boolean isShow() {
        if (customDialog != null) {
            return customDialog.isShowing();
        }
        return false;
    }
}