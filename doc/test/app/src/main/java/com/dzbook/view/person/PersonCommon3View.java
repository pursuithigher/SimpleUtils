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

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * PersonCommon3View
 *
 * @author lizz on 2017/12/5.
 */

public class PersonCommon3View extends RelativeLayout {

    private Context mContext;


    private TextView mTextViewTitle, mTextViewClick;
    private TextView mTextViewContent;
    private ImageView mImageViewIcon;

    /**
     * 构造
     *
     * @param context context
     */
    public PersonCommon3View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PersonCommon3View(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {

    }

    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_person_common3, this);
        mImageViewIcon = view.findViewById(R.id.imageView_icon);
        mTextViewTitle = view.findViewById(R.id.textview_title);
        mTextViewContent = view.findViewById(R.id.textview_content);
        mTextViewClick = view.findViewById(R.id.textview_click);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PersonCommon3View, 0, 0);
            if (array != null) {

                String title = array.getString(R.styleable.PersonCommon3View_person_common3_view_title);
                setTitle(title);

                String content = array.getString(R.styleable.PersonCommon3View_person_common3_view_content);
                mTextViewContent.setText(content);

                Drawable drawable = array.getDrawable(R.styleable.PersonCommon3View_person_common3_view_icon);
                if (drawable != null) {
                    mImageViewIcon.setImageDrawable(drawable);
                }

                int color = array.getColor(R.styleable.PersonCommon3View_person_common3_view_button_color, CompatUtils.getColor(getContext(), R.color.color_706ec5));
                if (color != 0 && color != -1) {
                    mTextViewClick.setTextColor(color);
                }

                Drawable buttonDrawable = array.getDrawable(R.styleable.PersonCommon3View_person_common3_view_button_drawable);
                if (buttonDrawable != null) {
                    CompatUtils.setBackgroundDrawable(mTextViewClick, buttonDrawable);
                }

                String rightText = array.getString(R.styleable.PersonCommon3View_person_common3_view_right_text);
                if (!TextUtils.isEmpty(rightText)) {
                    mTextViewClick.setText(rightText);
                }
                array.recycle();
            }
        }
    }

    /**
     * 设置title
     *
     * @param title title
     */
    public void setTitle(String title) {
        if (mTextViewTitle != null) {
            mTextViewTitle.setText(title);
        }

        if (mImageViewIcon.getVisibility() == View.GONE) {
            LayoutParams params = (LayoutParams) mTextViewTitle.getLayoutParams();
            params.setMargins(DimensionPixelUtil.dip2px(mContext, 5), 0, 0, 0);
            mTextViewTitle.setLayoutParams(params);
        }
    }

}
