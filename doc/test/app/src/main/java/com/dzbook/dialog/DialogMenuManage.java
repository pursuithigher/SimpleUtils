package com.dzbook.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import java.util.List;

/**
 * DialogMenuManage
 *
 * @author lizhongzhong 2017/4/7.
 */
public class DialogMenuManage extends PopupWindow implements View.OnClickListener {

    private int rootWidth;
    private int padding;
    private int buttonHeight;

    private List<String> dataList;
    private LinearLayout radioGroup;
    private Activity mActivity;

    private OnItemClickListener onItemClickListener;

    /**
     * DialogMenuManage
     * @param mActivity mActivity
     * @param width width
     */
    public DialogMenuManage(Activity mActivity, int width) {
        super(mActivity);
        init(mActivity, width);
    }

    private void init(Activity activity, int width) {
        rootWidth = DimensionPixelUtil.dip2px(activity, width);

        this.mActivity = activity;
        padding = DimensionPixelUtil.dip2px(activity, 16);
        buttonHeight = DimensionPixelUtil.dip2px(activity, 48);
        radioGroup = new LinearLayout(activity);
        radioGroup.setLayoutParams(new ViewGroup.LayoutParams(rootWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.setBackground(CompatUtils.getDrawable(activity, R.drawable.shap_dialog_menu_bg));
        int aPadding = 1;
        radioGroup.setPadding(aPadding, aPadding, aPadding, aPadding);
        this.setBackgroundDrawable(new ColorDrawable(CompatUtils.getColor(activity, android.R.color.transparent)));
        this.setOutsideTouchable(true);
        setContentView(radioGroup);
        initListener();
    }

    /**
     * setData
     * @param list list
     */
    public void setData(List<String> list) {
        if (list != null) {
            dataList = list;
            bindData();
        }
    }

    private void initListener() {
    }

    private void bindData() {
        for (int i = 0; dataList != null && i < dataList.size(); i++) {
            Button radioButton = new Button(mActivity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(rootWidth, buttonHeight);
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(dataList.get(i));
            radioButton.setTextColor(CompatUtils.getColor(mActivity, R.color.black_full));
            radioButton.setTextSize(15);
            radioButton.setMaxLines(2);
            radioButton.setEllipsize(TextUtils.TruncateAt.END);
            radioButton.setPadding(padding, 0, padding, 0);
            radioButton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            if (i == 0) {
                radioButton.setBackground(CompatUtils.getDrawable(mActivity, R.drawable.com_common_item_selector3));
            } else if (i == dataList.size() - 1) {
                radioButton.setBackground(CompatUtils.getDrawable(mActivity, R.drawable.com_common_item_selector4));
            } else {
                radioButton.setBackground(CompatUtils.getDrawable(mActivity, R.drawable.com_common_item_selector));
            }
            radioButton.setTag(i);
            radioButton.setOnClickListener(this);
            radioGroup.addView(radioButton);
            if (i != dataList.size() - 1) {
                // 画线
                View lineView = new View(mActivity);
                LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(rootWidth - 2 * padding, 1);
                lineLayoutParams.leftMargin = padding;
                lineView.setLayoutParams(lineLayoutParams);
                lineView.setBackgroundColor(Color.parseColor("#33182233"));
                radioGroup.addView(lineView);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.clickIndex((Integer) view.getTag());
        }
        dismiss();
    }

    public void setItemClickListener(OnItemClickListener clickListener) {
        this.onItemClickListener = clickListener;
    }

    /**
     * OnItemClickListener
     */
    public interface OnItemClickListener {
        /**
         * 返回点击位置
         *
         * @param index 点击位置
         */
        void clickIndex(int index);
    }
}
