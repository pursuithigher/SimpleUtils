package com.dzbook.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ishugui.R;

import hw.sdk.net.bean.reader.BeanBookRecomment;

/**
 * 推荐顶部视图
 * author lizhongzhong 2018/3/10.
 */

public class ChaseRecommendTopView extends LinearLayout {

    private ImageView imageviewIcon;

    private TextView textviewTips;

    private Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public ChaseRecommendTopView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ChaseRecommendTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public ChaseRecommendTopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_chase_recommend_top, this);

        imageviewIcon = view.findViewById(R.id.imageview_icon);
        textviewTips = view.findViewById(R.id.textview_tips);
    }


    private void initData() {
        textviewTips.setText("");
    }

    private void setListener() {

    }

    /**
     * 绑定数据
     *
     * @param beanInfo beanInfo
     */
    public void bindData(BeanBookRecomment beanInfo) {
        if (beanInfo != null) {
            if (!TextUtils.isEmpty(beanInfo.tip)) {
                textviewTips.setText(beanInfo.tip);
            }

            if (beanInfo.isEndBook()) {
                imageviewIcon.setImageResource(R.drawable.ic_reader_error_chapter);
            } else if (beanInfo.isSerialBook()) {
                imageviewIcon.setImageResource(R.drawable.ic_reader_error_chapter);
            }
        }
    }


}
