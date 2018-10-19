package com.dzbook.view.person;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ishugui.R;

/**
 * 通用样式
 *
 * @author kongxp on 2018/4/18.
 */

public class PersonCommonIconTextView extends RelativeLayout {

    private Context mContext;
    private ImageView mImageViewIcon;
    private TextView mTextViewName, mTextViewAngleTxt;
    private LinearLayout mLinearAngleBg;

    /**
     * 构造
     *
     * @param context context
     */
    public PersonCommonIconTextView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PersonCommonIconTextView(Context context, AttributeSet attrs) {
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

    /**
     * setNameTxt
      * @param text text
     */
    public void setNameTxt(String text) {
        if (mTextViewName != null) {
            mTextViewName.setText(text);
        }
    }

    /**
     * setAngleTxt
      * @param content content
     */
    public void setAngleTxt(String content) {
        mTextViewAngleTxt.setText(content);
    }

    /**
     * setAngleVisible
     * @param visible visible
     */
    public void setAngleVisible(int visible) {
        mLinearAngleBg.setVisibility(visible);
    }

    /**
     * setIconVisible
     * @param visible visible
     */
    public void setIconVisible(int visible) {
        mImageViewIcon.setVisibility(visible);
    }

    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_person_common_icon_textview, this);
        RelativeLayout mRelativeBg = view.findViewById(R.id.rl_view_bg);
        mImageViewIcon = view.findViewById(R.id.img_view_icon);
        mTextViewName = view.findViewById(R.id.tv_view_name);
        mTextViewAngleTxt = view.findViewById(R.id.tv_angle_txt);
        mLinearAngleBg = view.findViewById(R.id.ll_angle_bg);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PersonCommonIconTextView, 0, 0);
            if (array != null) {
                String title = array.getString(R.styleable.PersonCommonIconTextView_person_common_icon_text_view_name);
                setNameTxt(title);
                if (mLinearAngleBg.getVisibility() == VISIBLE) {
                    Drawable linearAngleBg = array.getDrawable(R.styleable.PersonCommonIconTextView_person_common_icon_text_view_angle_bg);
                    if (linearAngleBg != null) {
                        mLinearAngleBg.setBackground(linearAngleBg);
                    }
                    String angleTxt = array.getString(R.styleable.PersonCommonIconTextView_person_common_icon_text_view_angle_txt);
                    setAngleTxt(angleTxt);
                }
                Drawable relativeBg = array.getDrawable(R.styleable.PersonCommonIconTextView_person_common_icon_text_view_bg);
                if (relativeBg != null) {
                    mRelativeBg.setBackground(relativeBg);
                }
                Drawable imageViewIcon = array.getDrawable(R.styleable.PersonCommonIconTextView_person_common_icon_text_view_icon);
                if (null != imageViewIcon) {
                    mImageViewIcon.setVisibility(View.VISIBLE);
                    mImageViewIcon.setImageDrawable(imageViewIcon);
                }
                int alphaSize = array.getInteger(R.styleable.PersonCommonIconTextView_person_common_icon_text_view_item_icon_alpha, 255);
                if (null != imageViewIcon) {
                    mImageViewIcon.setImageAlpha(alphaSize);
                }
                boolean isShowAngle = array.getBoolean(R.styleable.PersonCommonIconTextView_angle_show, false);
                if (isShowAngle) {
                    setAngleVisible(VISIBLE);
                } else {
                    setAngleVisible(GONE);
                }

                boolean isIconAngle = array.getBoolean(R.styleable.PersonCommonIconTextView_icon_text_view_icon_show, false);
                if (isIconAngle) {
                    setIconVisible(VISIBLE);
                } else {
                    setIconVisible(GONE);
                }
                array.recycle();
            }
        }
    }
}
