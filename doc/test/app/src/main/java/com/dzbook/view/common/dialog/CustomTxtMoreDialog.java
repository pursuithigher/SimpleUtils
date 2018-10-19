package com.dzbook.view.common.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.view.common.CustomCheckTxtView;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;

import java.util.List;

/**
 * Dialog： 单选列表样式(TxtView)
 *
 * @author wangjianchen
 */
public class CustomTxtMoreDialog extends CustomDialogBusiness {

    private LinearLayout radioGroup;
    private List<String> dataList;

    /**
     * 是否点中view 后直接返回结果
     */
    private boolean clickConfirm = false;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                CustomCheckTxtView childView = (CustomCheckTxtView) radioGroup.getChildAt(i);
                childView.setChecked(i == (int) v.getTag());
            }
            if (clickConfirm) {
                clickConfirmEvent();
            }
        }
    };

    /**
     * CustomTxtMoreDialog
     * @param context context
     */
    public CustomTxtMoreDialog(Context context) {
        super(context);
    }

    /**
     * CustomTxtMoreDialog
     * @param context context
     * @param style style
     */
    public CustomTxtMoreDialog(Context context, int style) {
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
    protected void getCancelEvent() {

    }

    /**
     * 设置数据源
     *
     * @param click   true：点击选项即返回
     * @param strings strings
     */
    public void setData(List<String> strings, boolean click) {
        dataList = strings;
        this.clickConfirm = click;
        bindData(-1);
    }

    /**
     * 设置数据源
     *
     * @param checkIndex 从0开始， 选中位置
     * @param click      true：点击选项即返回
     * @param stringList stringList
     */
    public void setData(List<String> stringList, int checkIndex, boolean click) {
        dataList = stringList;
        this.clickConfirm = click;
        bindData(checkIndex);
    }

    /**
     * 设置数据源
     *
     * @param stringList stringList
     */
    public void setData(List<String> stringList) {
        dataList = stringList;
        this.clickConfirm = false;
        bindData(-1);
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


    private void bindData(int checkIndex) {
        int height = DimensionPixelUtil.dip2px(context, 48);
        for (int i = 0; dataList != null && i < dataList.size(); i++) {
            CustomCheckTxtView radioButton = new CustomCheckTxtView(context);
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
            ((CustomCheckTxtView) radioGroup.getChildAt(checkIndex)).setChecked(true);
        }
    }


}