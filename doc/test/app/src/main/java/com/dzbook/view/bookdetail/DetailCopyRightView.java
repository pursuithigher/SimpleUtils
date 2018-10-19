package com.dzbook.view.bookdetail;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * 版权信息
 *
 * @author dongdianzhou on 2017/10/24.
 */

public class DetailCopyRightView extends LinearLayout {
    private TextView mTextviewCopyright;
    private TextView mTextviewDisclaimer;

    /**
     * 构造
     *
     * @param context context
     */
    public DetailCopyRightView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DetailCopyRightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_detailcopyright, this);
        mTextviewCopyright = view.findViewById(R.id.textview_copyright);
        mTextviewDisclaimer = view.findViewById(R.id.textview_disclaimer);
        TextView tvBookMoreInfo = view.findViewById(R.id.tv_book_more_info);
        TypefaceUtils.setHwChineseMediumFonts(tvBookMoreInfo);
    }

    /**
     * 绑定数据
     *
     * @param bookDetailInfoResBean bookDetailInfoResBean
     */
    public void bindData(BeanBookInfo bookDetailInfoResBean) {
        if (!TextUtils.isEmpty(bookDetailInfoResBean.bookCopyright)) {
            mTextviewCopyright.setText(bookDetailInfoResBean.bookCopyright);
            mTextviewCopyright.setVisibility(VISIBLE);
        } else {
            mTextviewCopyright.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(bookDetailInfoResBean.bookDisclaimer)) {
            mTextviewDisclaimer.setText(bookDetailInfoResBean.bookDisclaimer);
            mTextviewDisclaimer.setVisibility(VISIBLE);
        } else {
            mTextviewDisclaimer.setVisibility(GONE);
        }
    }
}
