package com.dzbook.view.store;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.PageView.PageState;
import com.ishugui.R;

import huawei.widget.HwProgressBar;

/**
 * LD0View
 *
 * @author dongdianzhou on 2018/1/11.
 */

public class LD0View extends RelativeLayout {

    private HwProgressBar loading;
    private TextView textView;
    private PageState mState;

    /**
     * 构造
     *
     * @param context context
     */
    public LD0View(Context context) {
        super(context);
        initView(context, null);
    }

    /**
     * 构造
     *
     * @param context          context
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     */
    public LD0View(Context context, Fragment fragment, TempletPresenter templetPresenter) {
        super(context);
        initView(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public LD0View(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 88));
        setLayoutParams(layoutParams);
        setGravity(Gravity.CENTER);
        //        int padding = DimensionPixelUtil.dip2px(getContext(), 24);
        //        setPadding(0, padding, 0, padding);
        LayoutInflater.from(context).inflate(R.layout.page_loading_footer, this);
        //        setState(PageState.Loadable);
        loading = findViewById(R.id.loadingTemp);
        textView = findViewById(R.id.textview);
    }

    /**
     * 设置状态
     *
     * @param state state
     */
    public void setState(PageState state) {
        if (state == mState) {
            return;
        } else {
            mState = state;
        }
        switch (mState) {
            case Loadable:
                setVisibility(View.GONE);
                if (loading != null) {
                    loading.setVisibility(View.GONE);
                }
                break;
            case Empty:
                setVisibility(View.GONE);
                if (loading != null) {
                    loading.setVisibility(View.GONE);
                }
                break;
            case Loading:
                setVisibility(View.VISIBLE);
                if (loading != null) {
                    loading.setVisibility(View.VISIBLE);
                }
                textView.setText(R.string.str_loading);
                break;
            case End:
                setVisibility(View.GONE);
                if (loading != null) {
                    loading.setVisibility(View.GONE);
                }
                break;
            case Failed:
                if (loading != null) {
                    loading.setVisibility(View.GONE);
                }
                if (!NetworkUtils.getInstance().checkNet()) {
                    setVisibility(View.GONE);
                } else {
                    setVisibility(View.VISIBLE);
                    textView.setText(R.string.str_load_fail_retry);
                }
                break;
            default:
                break;
        }
    }

    public PageState getState() {
        return mState;
    }
}
