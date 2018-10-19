package com.dzbook.view.consume;

import android.app.Activity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.account.ConsumeBookSumAdapter;
import com.dzbook.mvp.presenter.ConsumeBookSumPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * ConsumeOtherSumView
 *
 * @author lizz 2018/4/20.
 */

public class ConsumeOtherSumView extends RelativeLayout {

    private Activity mActivity;

    private TextView tvOtherName;

    private ImageView ivOtherIcon;

    private View mLine;

    private ConsumeBookSumPresenter mPresenter;

    private ConsumeBookSumAdapter.OtherConsumeBean mBean;

    /**
     * 构造
     *
     * @param activity activity
     */
    public ConsumeOtherSumView(Activity activity) {
        this(activity, null);
    }

    /**
     * 构造
     *
     * @param activity activity
     * @param attrs    attrs
     */
    public ConsumeOtherSumView(Activity activity, AttributeSet attrs) {
        super(activity, attrs);
        mActivity = activity;
        initView();
        initData();
        setListener();
    }

    public void setMainShelfPresenter(ConsumeBookSumPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        int height = DimensionPixelUtil.dip2px(mActivity, 64);
        int padding = DimensionPixelUtil.dip2px(mActivity, 16);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(layoutParams);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_consume_other_sum, this);
        setPadding(padding, 0, padding, 0);

        tvOtherName = view.findViewById(R.id.tv_other_name);
        ivOtherIcon = view.findViewById(R.id.iv_other_icon);
        mLine = findViewById(R.id.v_other_line);
    }

    private void initData() {

    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPresenter != null && mBean != null && !TextUtils.isEmpty(mBean.typeId)) {
                    mPresenter.launchSecondConsume(mBean.typeId, "");
                }
            }
        });
    }

    /**
     * 绑定数据
     *
     * @param bean          bean
     * @param isShowEndLine isShowEndLine
     */
    public void bindData(ConsumeBookSumAdapter.OtherConsumeBean bean, boolean isShowEndLine) {
        if (bean != null) {
            mBean = bean;
            tvOtherName.setText(bean.mTitle);
            ivOtherIcon.setImageResource(bean.mIconRes);
            if (isShowEndLine) {
                mLine.setVisibility(GONE);
            } else {
                mLine.setVisibility(VISIBLE);

            }
        }
    }

}
