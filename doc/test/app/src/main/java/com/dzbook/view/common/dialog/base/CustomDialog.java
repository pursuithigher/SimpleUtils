package com.dzbook.view.common.dialog.base;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ishugui.R;

/**
 * Dialog： Window窗口级别属性、逻辑封装
 *
 * @author wangjianchen
 */
public class CustomDialog extends CustomDialogBase {
    private int width;
    private int height;
    private int locatinX;
    private int locationY;
    private int gravityStyle;
    private int animStyle;
    private boolean isTouchOut;
    private View rootView;

    /**
     * 构造
     *
     * @param builder builder
     */
    protected CustomDialog(Builder builder) {
        super(builder.context);
        initData(builder);
    }

    /**
     * 构造
     *
     * @param builder  builder
     * @param resStyle resStyle
     */
    protected CustomDialog(Builder builder, int resStyle) {
        super(builder.context, resStyle);
        initData(builder);
    }

    private void initData(Builder builder) {
        width = builder.width;
        height = builder.height;
        locationY = builder.locationY;
        locatinX = builder.locatinX;
        isTouchOut = builder.isTouchOut;
        rootView = builder.rootView;
        gravityStyle = builder.gravityStyle;
        animStyle = builder.animStyle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (rootView != null) {
            setCenterView(rootView);
        }
        initDefaultSetting();
    }

    /**
     * 基本 Style
     */
    private void initDefaultSetting() {
        setCanceledOnTouchOutside(isTouchOut);
        Window win = getWindow();
        if (win != null) {
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.gravity = gravityStyle;
            lp.x = locatinX;
            lp.y = locationY;
            if (height > 0) {
                lp.height = height;
            } else {
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            if (width > 0) {
                lp.width = width;
            } else {
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            }
            win.setAttributes(lp);
            win.setWindowAnimations(animStyle);
        }

    }

    /**
     * proxy 模式， 方便外面调用
     */
    public static final class Builder {
        private boolean isTouchOut;
        private int width;
        private int height;
        private int locatinX = -1;
        private int locationY = -1;
        private int gravityStyle;
        private int animStyle;

        private Context context;
        private View rootView;

        /**
         * 构造
         *
         * @param context context
         */
        public Builder(Context context) {
            this.context = context;
            this.gravityStyle = Gravity.BOTTOM;
            this.animStyle = R.style.dialog_normal;
            this.isTouchOut = true;
        }

        /**
         * 加载view
         *
         * @param resView v
         * @return Builder
         */
        public Builder view(int resView) {
            rootView = LayoutInflater.from(context).inflate(resView, null);
            return this;
        }

        /**
         * 加载view
         *
         * @param view view
         * @return Builder
         */
        public Builder view(View view) {
            rootView = view;
            return this;
        }

        /**
         * 显示动画
         *
         * @param style style
         * @return Builder
         */
        public Builder animStyle(int style) {
            animStyle = style;
            return this;
        }

        /**
         * 设置显示的x 坐标
         *
         * @param x x
         * @return Builder
         */
        public Builder x(int x) {
            locatinX = x;
            return this;
        }

        /**
         * 设定显示的y 坐标
         *
         * @param y y
         * @return Builder
         */
        public Builder y(int y) {
            locationY = y;
            return this;
        }

        /**
         * 设定view 的高度
         *
         * @param h height
         * @return Builder
         */
        public Builder height(int h) {
            this.height = h;
            return this;
        }

        /**
         * 设定view 的宽度
         *
         * @param w width
         * @return Builder
         */
        public Builder width(int w) {
            this.width = w;
            return this;
        }

        /**
         * 设定view 的高度
         *
         * @param h height
         * @return Builder
         */
        public Builder heightDimen(int h) {
            this.height = context.getResources().getDimensionPixelOffset(h);
            return this;
        }

        /**
         * 设定view 的高度
         *
         * @param w width
         * @return Builder
         */
        public Builder widthDimen(int w) {
            this.width = context.getResources().getDimensionPixelOffset(w);
            return this;
        }

        /**
         * 设定显示位置
         *
         * @param style style
         * @return Builder
         */
        public Builder setGravity(int style) {
            this.gravityStyle = style;
            return this;
        }

        /**
         * 是否可以点击屏幕外
         *
         * @param isTrue isTrue
         * @return Builder
         */
        public Builder isCanTouchOut(boolean isTrue) {
            isTouchOut = isTrue;
            return this;
        }

        /**
         * 构建CustomDialog
         *
         * @return CustomDialog
         */
        public CustomDialog build() {
            if (gravityStyle != -1) {
                return new CustomDialog(this, gravityStyle);
            } else {
                return new CustomDialog(this);
            }
        }
    }
}