package com.dzbook.view.person;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.PersonCenterPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * 设置条目布局
 *
 * @author dongdianzhou on 2017/4/5.
 */

public class PersonCommon2View extends RelativeLayout {

    private Context mContext;

    private TextView mTextViewTitle;
    private TextView mTextViewContent;
    private ImageView mImageViewIcon;
    private ImageView mImageViewMark;
    private View mImagViewLine;

    /**
     * 构造
     *
     * @param context context
     */
    public PersonCommon2View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PersonCommon2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
        initData();
        setListener();
    }

    /**
     * 设置Presenter
     *
     * @param mPresenter mPresenter
     */
    public void setPresenter(PersonCenterPresenter mPresenter) {
    }

    private void setListener() {

    }

    private void initData() {

    }

    /**
     * 设置文字
     *
     * @param text text
     */
    public void setContentText(String text) {
        if (mTextViewContent != null) {
            mTextViewContent.setText(text);
        }
    }

    /**
     * 设置是否显示
     *
     * @param visible visible
     */
    public void setContentVisible(int visible) {
        mTextViewContent.setVisibility(visible);
    }

    private void initView(AttributeSet attrs) {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_person_phone, this);
        mImageViewIcon = view.findViewById(R.id.imageView_icon);
        mImageViewMark = view.findViewById(R.id.imageview_mark);
        mTextViewContent = view.findViewById(R.id.textview_content);
        mTextViewTitle = view.findViewById(R.id.textview_title);
        mImagViewLine = view.findViewById(R.id.imageview_line);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PersonCommon2View, 0, 0);
            if (array != null) {

                boolean isShowLine = array.getBoolean(R.styleable.PersonCommon2View_person_common2_view_line_show, true);
                if (isShowLine) {
                    mImagViewLine.setVisibility(VISIBLE);
                } else {
                    mImagViewLine.setVisibility(GONE);
                }

                String content = array.getString(R.styleable.PersonCommon2View_person_common2_view_content);
                mTextViewContent.setText(content);
                boolean isShowIcon = array.getBoolean(R.styleable.PersonCommon2View_person_common2_isShowIcon, true);
                if (isShowIcon) {
                    mImageViewIcon.setVisibility(VISIBLE);
                } else {
                    mImageViewIcon.setVisibility(GONE);
                }
                boolean isShowMark = array.getBoolean(R.styleable.PersonCommon2View_person_common2_isShowMark, true);
                if (isShowMark) {
                    mImageViewMark.setVisibility(VISIBLE);
                } else {
                    mImageViewMark.setVisibility(GONE);
                }

                String title = array.getString(R.styleable.PersonCommon2View_person_common2_view_title);
                setTitle(title);

                array.recycle();
            }
        }
    }

    /**
     * 设置标题
     *
     * @param title title
     */
    public void setTitle(String title) {
        if (mTextViewTitle != null) {
            mTextViewTitle.setText(title);
        }

        if (mImageViewIcon.getVisibility() == View.GONE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTextViewTitle.getLayoutParams();
            params.setMargins(DimensionPixelUtil.dip2px(mContext, 15), 0, 0, 0);
            mTextViewTitle.setLayoutParams(params);
        }
    }
}
