package com.views.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.interf.MeasureSpaceCallBack;
import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-5-18.
 *
 * custom attrs
 * 1: res/values/attrs.xml see simple
 *
 */
public class SimpleExtendView extends View {

    private String customTitle,customSubTitle;

    /**
     * 计算的title大小，以向右向下为正数
     */
    private Rect textBounds;
    private int background;
    private Drawable icon;
    private Paint mPaint;

    public SimpleExtendView(Context context) {
        this(context,null);
    }

    public SimpleExtendView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleExtendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialStyle(context,attrs,defStyleAttr,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SimpleExtendView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialStyle(context,attrs,defStyleAttr,defStyleRes);
    }

    private void initialStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.customView,defStyleAttr,defStyleRes);
        int count = a.getIndexCount();

        for(int i=0;i<count;i++){
            int attr = a.getIndex(i);
            switch(attr){
                case R.styleable.customView_customTitle:
                    customTitle = a.getString(attr);
                    break;
                case R.styleable.customView_customBackIcon:
                    icon = a.getDrawable(attr);
                    break;
                case R.styleable.customView_customSubTitle:
                    customSubTitle = a.getString(attr);
                    break;
                case R.styleable.customView_customBackGround:
                    background = a.getColor(attr,Color.WHITE);
                    break;
            }
        }
        a.recycle();


        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(40);
        textBounds = new Rect();
        mPaint.getTextBounds(customTitle,0,customTitle.length(),textBounds);
    }

    private MeasureSpaceCallBack spaceCallBack = new MeasureSpaceCallBack() {
        @Override
        public int getContentWitdh() {
            return getViewWitdh();
        }

        @Override
        public int getContentHeight() {
            return getViewHeight();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] result = MeasureSpaceCallBack.measureSize(widthMeasureSpec,heightMeasureSpec,spaceCallBack);
        setMeasuredDimension(result[0],result[1]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(background);

        //宽度使用-,高度使用+,向下为正数
        canvas.drawText(customTitle,(getWidth()-textBounds.width())/2,(getHeight()+textBounds.height())/2,mPaint);
    }

    private int getViewWitdh(){
        return 300;
    }

    private int getViewHeight(){
        return 200;
    }

}
