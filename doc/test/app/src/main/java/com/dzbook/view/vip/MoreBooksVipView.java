package com.dzbook.view.vip;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.store.CommonTwoLevelActivity;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.vip.VipBookInfo;

/**
 * 更多布局
 *
 * @author gavin
 */
public class MoreBooksVipView extends RelativeLayout {

    private Context mContext;
    private int type = -1;
    private TextView mTextView;
    private RelativeLayout relativeLayoutMore;

    /**
     * 构造
     *
     * @param context context
     * @param type type
     */
    public MoreBooksVipView(Context context, int type) {
        this(context, null, type);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     * @param position   position
     */
    public MoreBooksVipView(Context context, AttributeSet attrs, int position) {
        super(context, attrs);
        mContext = context;
        type = position;
        initView();
        initData();
        setListener();
    }

    private void initView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (type == 0) {
            layoutParams.topMargin = DimensionPixelUtil.dip2px(mContext, 20);
        } else {
            layoutParams.topMargin = DimensionPixelUtil.dip2px(mContext, 28);
        }
        layoutParams.leftMargin = DimensionPixelUtil.dip2px(mContext, 16);
        layoutParams.rightMargin = DimensionPixelUtil.dip2px(mContext, 18);
        setLayoutParams(layoutParams);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_sj_moretitle, this);
        mTextView = view.findViewById(R.id.textview_title);
        relativeLayoutMore = view.findViewById(R.id.relativelayout_more);
        TypefaceUtils.setHwChineseMediumFonts(mTextView);
    }

    private void setListener() {

    }

    private void initData() {

    }

    /**
     * 绑定数据
     *
     * @param info info
     */
    public void bindData(final VipBookInfo.TitleBean info) {
        if (info != null) {
            if (!TextUtils.isEmpty(info.title)) {
                mTextView.setText(info.title);
            }
            if (!TextUtils.isEmpty(info.actionDataId)) {
                setMoreViewVisible(VISIBLE);
                setMoreClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonTwoLevelActivity.launch((Activity) mContext, info.title, info.actionDataId);
                    }
                });
            } else {
                setMoreViewVisible(GONE);
            }
        }
    }

    /**
     * 更多按钮点击事件
     *
     * @param onClickListener onClickListener
     */
    public void setMoreClickListener(OnClickListener onClickListener) {
        relativeLayoutMore.setOnClickListener(onClickListener);
    }

    /**
     * 更多按钮是否显示
     *
     * @param visible visible
     */
    public void setMoreViewVisible(int visible) {
        relativeLayoutMore.setVisibility(visible);
    }
}

