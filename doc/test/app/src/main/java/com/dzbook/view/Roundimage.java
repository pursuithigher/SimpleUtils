package com.dzbook.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dzbook.utils.DimensionPixelUtil;

/**
 * Roundimage
 *
 * @author gavin
 */
@SuppressLint("AppCompatCustomView")
public class Roundimage extends ImageView {

    float rid = DimensionPixelUtil.dip2px(getContext(), 4f);

    private float[] rids = {rid, rid, rid, rid, 0.0f, 0.0f, 0.0f, 0.0f,};


    /**
     * 构造
     *
     * @param context context
     */
    public Roundimage(Context context) {
        super(context);
    }


    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Roundimage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public Roundimage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        path.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}