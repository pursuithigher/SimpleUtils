package com.dzbook.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ishugui.R;

/**
 * DianzhongDefaultView
 *
 * @author dongdianzhou on 2017/4/10.
 */

public class DianzhongDefaultView extends RelativeLayout {

    /**
     * 操作类型 操作按钮刷新
     */
    public static final int OPERATE_REFRESH_TYPE = 1;

    /**
     * 操作类型 操作是去书城逛逛
     */
    public static final int OPERATE_GO_BOOK_STORE = OPERATE_REFRESH_TYPE + 1;

    private ImageView mImageviewMark;
    private TextView mTextviewTitle;
    private TextView mTextviewOper;

    private int oprateType = -1;

    /**
     * 构造
     *
     * @param context context
     */
    public DianzhongDefaultView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DianzhongDefaultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_defaultview, this);
        mImageviewMark = view.findViewById(R.id.imageview_mark);
        mTextviewTitle = view.findViewById(R.id.textview_title);
        mTextviewOper = view.findViewById(R.id.textview_oper);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.DianzhongDefaultView, 0, 0);
            if (array != null) {
                Drawable drawable = array.getDrawable(R.styleable.DianzhongDefaultView_default_view_mark);
                if (drawable != null) {
                    mImageviewMark.setImageDrawable(drawable);
                }
                String title = array.getString(R.styleable.DianzhongDefaultView_default_view_title);
                mTextviewTitle.setText(title);
                String oper = array.getString(R.styleable.DianzhongDefaultView_default_view_oper);
                mTextviewOper.setText(oper);
                boolean isShowOper = array.getBoolean(R.styleable.DianzhongDefaultView_default_view_oper_show, false);
                if (isShowOper) {
                    mTextviewOper.setVisibility(VISIBLE);
                } else {
                    mTextviewOper.setVisibility(GONE);
                }
                array.recycle();
            }
        }
    }

    /**
     * 设置监听
     *
     * @param operClickListener operClickListener
     */
    public void setOperClickListener(OnClickListener operClickListener) {
        if (mTextviewOper != null && mTextviewOper.getVisibility() == VISIBLE) {
            mTextviewOper.setOnClickListener(operClickListener);
        }
    }

    /**
     * 设置图片
     *
     * @param resouceId resouceId
     */
    public void setImageviewMark(int resouceId) {
        mImageviewMark.setImageResource(resouceId);
    }

    /**
     * 设置描述
     *
     * @param title title
     */
    public void settextViewTitle(String title) {
        mTextviewTitle.setText(title);
    }

    /**
     * 设置按钮显示内容
     *
     * @param oper oper
     */
    public void setTextviewOper(String oper) {
        mTextviewOper.setText(oper);
    }

    /**
     * 设置操作类型  这样判断按钮 是刷新操作 还是去书城逛逛
     * {@link DianzhongDefaultView#OPERATE_REFRESH_TYPE}
     * {@link DianzhongDefaultView#OPERATE_GO_BOOK_STORE}
     *
     * @param oprateType type
     */
    public void setOprateType(int oprateType) {
        this.oprateType = oprateType;
    }

    public int getOprateType() {
        return oprateType;
    }

    /**
     * 设置页面状态
     *
     * @param resouceId     resouceId
     * @param textViewTitle textViewTitle
     * @param textOper      textOper
     * @param type          oprateType
     */
    public void setPageStatus(int resouceId, String textViewTitle, String textOper, int type) {
        setVisibility(View.VISIBLE);
        setImageviewMark(resouceId);
        settextViewTitle(textViewTitle);
        setTextviewOper(textOper);
        setOprateType(type);
    }

    /**
     * 设置类型
     *
     * @param visible visible
     */
    public void setOprateTypeState(int visible) {
        mTextviewOper.setVisibility(visible);
    }
}
