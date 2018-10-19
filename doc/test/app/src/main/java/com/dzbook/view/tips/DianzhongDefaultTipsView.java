package com.dzbook.view.tips;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ishugui.R;

/**
 * DianzhongDefaultTipsView
 *
 * @author dongdianzhou on 2017/4/10.
 */

public class DianzhongDefaultTipsView extends RelativeLayout {

    private RecyclerImageView mImageviewMark;

    /**
     * 构造
     *
     * @param context context
     */
    public DianzhongDefaultTipsView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DianzhongDefaultTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_defaulttipsview, this);
        mImageviewMark = view.findViewById(R.id.imageview_mark);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.DianzhongDefaultTipsView, 0, 0);
            if (array != null) {
                int drawableRes = array.getResourceId(R.styleable.DianzhongDefaultTipsView_icon_mark, -10);
                if (drawableRes != -10) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableRes);
                    if (bitmap != null) {
                        mImageviewMark.setImageBitmap(bitmap);
                    }
                }
                array.recycle();
            }
        }
    }

    /**
     * 设置图片资源
     *
     * @param res res
     */
    public void setmImageviewMarkRes(int res) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
        if (bitmap != null) {
            mImageviewMark.setImageBitmap(bitmap);
        }
    }

    /**
     * 回收Bitmap
     */
    public void recyclerBitmap() {
        if (mImageviewMark != null) {
            mImageviewMark.recyclerBitmap();
        }
    }

}
