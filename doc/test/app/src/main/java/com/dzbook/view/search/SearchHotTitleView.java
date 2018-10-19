package com.dzbook.view.search;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * SearchHotTitleView
 *
 * @author dongdianzhou on 2017/3/29.
 */

public class SearchHotTitleView extends RelativeLayout {

    private Context mContext;
    private TextView mTextViewTitle;
    private TextView mTextViewOper;

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SearchHotTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SearchHotTitleView, 0, 0);
            if (array != null) {
                boolean isHistory = array.getBoolean(R.styleable.SearchHotTitleView_ishistory, false);
                initData(isHistory);
                array.recycle();
            }
        }
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_searchhot_title, this);
        mTextViewTitle = view.findViewById(R.id.textview_searchhot_title);
        mTextViewOper = view.findViewById(R.id.textview_searchhot_clear);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewTitle);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewOper);
    }

    private void initData(boolean isHistory) {
        if (isHistory) {
            mTextViewTitle.setText(mContext.getString(R.string.str_searchhot_history));
            mTextViewOper.setText(mContext.getString(R.string.str_clear));
        } else {
            mTextViewOper.setVisibility(GONE);
        }
    }

    public View getOperView() {
        return mTextViewOper;
    }


}
