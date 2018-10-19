package com.dzbook.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dzbook.bean.MainTabBean;
import com.dzbook.view.navigation.NavigationTabView;

import java.util.List;

/**
 * 底部导航布局
 *
 * @author dongdianzhou on 2017/11/28.
 */

public class NavigationLinearLayout extends LinearLayout {


    /**
     * 构造
     *
     * @param context context
     */
    public NavigationLinearLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public NavigationLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 添加自布局
     *
     * @param list list
     */
    public void addChildView(List<MainTabBean> list) {
        removeAllViews();
        for (MainTabBean sti : list) {
            if (sti != null) {
                boolean textgone = false;
                //                if (sti.isRecommend()) {
                //                    textgone = true;
                //                }
                NavigationTabView tabView = new NavigationTabView(getContext(), sti, textgone);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1.0f;
                //                if (sti.isRecommend()) {
                //                    tabView.setId(R.id.tab_recommend);
                //                    tabView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                //                }
                tabView.setLayoutParams(layoutParams);
                addView(tabView);
            }
        }
    }

}
