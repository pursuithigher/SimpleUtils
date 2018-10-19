package com.dzbook.view.tips;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * RecyclerImageView
 *
 * @author dongdianzhou on 2017/4/26.
 */

public class RecyclerImageView extends ImageView {

    private Bitmap bitmap;

    /**
     * 构造
     *
     * @param context context
     */
    public RecyclerImageView(Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RecyclerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public RecyclerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        bitmap = bm;
        super.setImageBitmap(bm);
    }

    /**
     * 回收
     */
    public void recyclerBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

}
