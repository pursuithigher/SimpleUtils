package com.dzbook.view.common.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.common.CustomCheckBox;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;

import java.util.List;

/**
 * Dialog： 单选列表样式
 *
 * @author wangjianchen
 */
public class CustomCheckMoreDialog extends CustomDialogBusiness {

    private LinearLayout radioGroup;
    private List<String> dataList;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                CustomCheckBox childView = (CustomCheckBox) radioGroup.getChildAt(i);
                childView.setChecked(i == (int) v.getTag());
            }
            if (clickConfirm) {
                clickConfirmEvent();
            }
        }
    };

    /**
     * 是否点中view 后直接返回结果
     */
    private boolean clickConfirm = false;

    /**
     * 构造
     *
     * @param context context
     */
    public CustomCheckMoreDialog(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param style   style
     */
    public CustomCheckMoreDialog(Context context, int style) {
        super(context, style);
    }

    @Override
    protected View getView() {
        radioGroup = new LinearLayout(context);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return radioGroup;
    }

    @Override
    protected Object getConfirmEvent() {
        int result = -1;
        if (radioGroup != null && radioGroup.getChildCount() > 0) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if ((radioGroup.getChildAt(i)).isSelected()) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected void getCancelEvent() {

    }

    /**
     * 设置数据源
     *
     * @param list list
     */
    public void setData(List<String> list) {
        dataList = list;
        this.clickConfirm = false;
        bindData(-1);
    }

    /**
     * 设置数据源
     *
     * @param click true：点击选项即返回
     * @param list  list
     */
    public void setData(List<String> list, boolean click) {
        dataList = list;
        this.clickConfirm = click;
        bindData(-1);
    }

    /**
     * 设置数据源
     *
     * @param checkIndex 从0开始， 选中位置
     * @param click      true：点击选项即返回
     * @param list       list
     */
    public void setData(List<String> list, int checkIndex, boolean click) {
        dataList = list;
        this.clickConfirm = click;
        bindData(checkIndex);
    }

    private void bindData(int checkIndex) {
        int height = DimensionPixelUtil.dip2px(context, 48);
        for (int i = 0; dataList != null && i < dataList.size(); i++) {
            CustomCheckBox radioButton = new CustomCheckBox(context);
            radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            radioButton.setDesc(dataList.get(i));
            if (i != dataList.size() - 1) {
                radioButton.setSupportLine(true);
            } else {
                radioButton.setSupportLine(false);
            }
            radioButton.setTag(i);
            radioButton.setOnClickListener(clickListener);
            radioGroup.addView(radioButton);
        }
        if (checkIndex >= 0 && checkIndex < radioGroup.getChildCount()) {
            ((CustomCheckBox) radioGroup.getChildAt(checkIndex)).setChecked(true);
        }
    }


}