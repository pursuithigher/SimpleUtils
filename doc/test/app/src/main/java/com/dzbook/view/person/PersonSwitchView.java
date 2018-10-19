package com.dzbook.view.person;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.view.DzSwitchButton;
import com.ishugui.R;

/**
 * SwitchView
 *
 * @author dongdianzhou on 2017/4/5.
 */
public class PersonSwitchView extends RelativeLayout {

    /**
     * mSwitchButton
     */
    public DzSwitchButton mSwitchButton;
    private Context mContext;
    private TextView mTextViewTitle;
    private ImageView mImageViewIcon;
    private ImageView mImageViewLine;

    /**
     * 构造
     *
     * @param context context
     */
    public PersonSwitchView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PersonSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
        initData();
        setListener();
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchButton.isChecked()) {
                    mSwitchButton.setChecked(false);
                } else {
                    mSwitchButton.setChecked(true);
                }
            }
        });
    }

    private void initData() {

    }

    private void initView(AttributeSet attrs) {
        boolean isShowIcon = false;
        boolean isShowLine = true;
        String viewTitle = "";

        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PersonSwitchView, 0, 0);
            if (array != null) {
                isShowIcon = array.getBoolean(R.styleable.PersonSwitchView_isShowIcon, true);
                isShowLine = array.getBoolean(R.styleable.PersonSwitchView_isShowLine, true);
                viewTitle = array.getString(R.styleable.PersonSwitchView_person_switch_view_title);
                array.recycle();
            }
        }

        View view = LayoutInflater.from(mContext).inflate(isShowIcon ? R.layout.view_person_item_icon_switch : R.layout.view_person_item_switch, this);
        mSwitchButton = view.findViewById(R.id.togglebutton_readmode);
        mTextViewTitle = view.findViewById(R.id.tv_title);
        mImageViewIcon = view.findViewById(R.id.imageView_icon);
        mImageViewLine = view.findViewById(R.id.imageview_line);

        setIconVisible(isShowIcon);
        setLineVisible(isShowLine);
        setTitle(viewTitle);
    }

    /**
     * 打开
     */
    public void openSwitch() {
        mSwitchButton.setChecked(true);
    }

    /**
     * 关闭
     */
    public void closedSwitch() {
        mSwitchButton.setChecked(false);
    }

    /**
     * 设置是否显示icon
     *
     * @param isShow isShow
     */
    public void setIconVisible(boolean isShow) {
        if (mImageViewIcon != null) {
            mImageViewIcon.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    private void setLineVisible(boolean isShow) {
        if (mImageViewLine != null) {
            mImageViewLine.setVisibility(isShow ? VISIBLE : GONE);
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
    }

}
