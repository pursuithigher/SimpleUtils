package com.views.ui.interfaceImpl;

import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by qzzhu on 16-11-10.
 * let view be moveable that set this onTouchListener
 * its botton limit only effect when NO_ACTION_BAR
 * 使用方法：使用view的setonTouchListener并且添加该Class实例
 */
public class MoveabelTouchListener implements View.OnTouchListener {

    private int lastX,lastY;
    private View ref;
    private int screenWidth,screenHeight;
    private boolean isclick;
    private View.OnClickListener clicklistener;


    public MoveabelTouchListener(@NonNull  View ref){
        this.ref = ref;
        DisplayMetrics metrics = ref.getContext().getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int ea = event.getAction();
        switch (ea) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();//按钮初始的横纵坐标
                isclick = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX()-lastX;
                int dy = (int) event.getRawY()-lastY;//按钮被移动的距离
                if(dx!=0 || dy !=0)
                {
                    isclick=true;//当按钮被移动的时候设置isclick为true
                }else{
                    break;
                }
                int l = ref.getLeft()+dx;
                int b = ref.getBottom()+dy;
                int r = ref.getRight()+dx;
                int t = ref.getTop()+dy;
                int vwidth = ref.getWidth();
                int vheight = ref.getHeight();
                if(l<0){//处理按钮被移动到上下左右四个边缘时的情况，决定着按钮不会被移动到屏幕外边去
                    l = 0;
                    r = l+vwidth;
                }
                if(t<0){
                    t = 0;
                    b = t+vheight;
                }
                if(r > screenWidth)
                {
                    r = screenWidth;
                    l = r - vwidth;
                }
                if(b > screenHeight)
                {
                    b = screenHeight;
                    t = b - vheight;
                }
                ref.layout(l, t, r, b);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                ref.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(!isclick)
                {
                    if(clicklistener != null)
                        clicklistener.onClick(ref);
                }
                break;
        }
        return true;
    }

    /**
     * 使这个View拥有点击事件
     * @param clicklistener
     * @return
     */
    public MoveabelTouchListener setOnClickListener(View.OnClickListener clicklistener){
        this.clicklistener = clicklistener;
        return this;
    }
}
