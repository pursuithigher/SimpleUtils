package com.dzbook.view.person;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.PersonCenterPresenter;
import com.ishugui.R;

/**
 * PersonCommonView
 *
 * @author dongdianzhou on 2017/4/5.
 */

public class PersonCommonView extends RelativeLayout {

    private Context mContext;

    private ImageView mImageViewIcon;
    private TextView mTextViewTitle;
    private TextView mTextViewContent;
    private View mImagViewLine;

    /**
     * 构造
     *
     * @param context context
     */
    public PersonCommonView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PersonCommonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
        initData();
        setListener();
    }

    /**
     * setPresenter
     *
     * @param mPresenter mPresenter
     */
    public void setPresenter(PersonCenterPresenter mPresenter) {
    }

    private void setListener() {

    }

    private void initData() {

    }

    private void initView(AttributeSet attrs) {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_person_common, this);
        mImageViewIcon = view.findViewById(R.id.imageView_icon);
        mTextViewContent = view.findViewById(R.id.textview_content);
        mTextViewTitle = view.findViewById(R.id.textview_title);
        mImagViewLine = view.findViewById(R.id.imageview_line);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PersonCommonView, 0, 0);
            if (array != null) {
                Drawable drawable = array.getDrawable(R.styleable.PersonCommonView_item_icon);
                if (drawable != null) {
                    mImageViewIcon.setImageDrawable(drawable);
                }

                boolean isShowLine = array.getBoolean(R.styleable.PersonCommonView_line_show, true);
                if (isShowLine) {
                    mImagViewLine.setVisibility(VISIBLE);
                } else {
                    mImagViewLine.setVisibility(GONE);
                }
                boolean isShowIcon = array.getBoolean(R.styleable.PersonCommonView_icon_show, true);
                if (isShowIcon) {
                    mImageViewIcon.setVisibility(VISIBLE);
                } else {
                    mImageViewIcon.setVisibility(GONE);
                }

                int alphaSize = array.getInteger(R.styleable.PersonCommonView_item_icon_alpha, 128);
                if (isShowIcon) {
                    mImageViewIcon.setImageAlpha(alphaSize);
                }

                String itemContent = array.getString(R.styleable.PersonCommonView_item_content);
                if (!TextUtils.isEmpty(itemContent)) {
                    mTextViewContent.setVisibility(VISIBLE);
                    mTextViewContent.setText(itemContent);
                } else {
                    mTextViewContent.setVisibility(GONE);
                }


                String title = array.getString(R.styleable.PersonCommonView_item_title);
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
    }

    /**
     * 设置内容
     *
     * @param itemContent itemContent
     */
    public void setTextViewContent(String itemContent) {
        if (!TextUtils.isEmpty(itemContent)) {
            mTextViewContent.setVisibility(VISIBLE);
            mTextViewContent.setText(itemContent);
        }
    }

    /**
     * 设置显示状态
     *
     * @param status status
     */
    public void setTextViewContentShowStatus(int status) {
        mTextViewContent.setVisibility(status);
    }

}
