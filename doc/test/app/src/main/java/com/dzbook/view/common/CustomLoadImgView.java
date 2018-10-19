package com.dzbook.view.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.dzbook.lib.utils.CompatUtils;
import com.ishugui.R;

/**
 * 描述：旋转View
 */
public class CustomLoadImgView extends View {

    private static final int ROTATE_STEP = 10;

    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
    private Bitmap mForeBitmap;
    private Matrix mMatrix = new Matrix();

    private int rotate;

    /**
     * 构造
     *
     * @param context context
     */
    public CustomLoadImgView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CustomLoadImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) CompatUtils.getDrawable(getContext(), R.drawable.hw_person_up_date);
        if (bitmapDrawable != null) {
            mForeBitmap = bitmapDrawable.getBitmap();
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mForeBitmap.getWidth(), mForeBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getVisibility() != VISIBLE) {
            return;
        }
        if (mForeBitmap != null && mForeBitmap.isRecycled()) {
            init();
        }
        if (mForeBitmap != null && !(mForeBitmap.isRecycled())) {
            mMatrix.setRotate((float) rotate, (float) mForeBitmap.getWidth() / 2, (float) mForeBitmap.getHeight() / 2);
            canvas.setDrawFilter(mPaintFlagsDrawFilter);
            // 绘制前景图
            canvas.drawBitmap(mForeBitmap, mMatrix, null);
            int rotateCache = rotate + ROTATE_STEP;
            rotate = rotateCache > 360 ? rotateCache - 360 : rotateCache;
            postInvalidate();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        invalidate();
    }
}