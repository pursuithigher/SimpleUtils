package com.dzbook.view;

import android.text.TextPaint;
import android.widget.TextView;

import java.util.List;

/**
 * 绘制工具类
 */
public class MeasureUtils {

    /**
     * 测绘频道的padding
     *
     * @param list        ：频道文本列表
     * @param textView    ：频道itemtextview
     * @param viewDis     ：item间距
     * @param leftPadding ：左padding
     * @param screenWidth ：频幕宽度
     * @param minWidth    ：最小宽度
     * @return 计算后的左padding：外面使用大于16dp使用这一个小于16dp使用16dp
     */
    public static int measureTabPadding(List<String> list, TextView textView, int viewDis, int leftPadding, int screenWidth, int minWidth) {
        if (list == null) {
            return 0;
        }
        int width = 0;
        TextPaint textPaint = textView.getPaint();
        for (String text : list) {
            float textWidth = textPaint.measureText(text);
            if (textWidth < minWidth) {
                textWidth = minWidth;
            }
            width += textWidth;
        }
        int size = list.size();
        width = width + viewDis * (size - 1);
        if (width > (screenWidth - leftPadding)) {
            //可滑动
            return leftPadding;
        } else {
            //不可滑动
            int padding = (screenWidth - width) / 2;
            return padding < leftPadding ? leftPadding : padding;
        }
    }


}
