package com.dzbook.view;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

/**
 * 本地导入上方的路径面包屑
 *
 * @author wangjianchen
 */
public class CustomFilePathView extends ViewGroup {

    private PathClickListener pathClickListener;

    private int paddingWidth;
    private int paddingTop;
    private boolean isShowComplete;
    private String rootDirPath;

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (pathClickListener != null) {
                pathClickListener.clickBack((String) v.getTag());
            }
        }
    };

    /**
     * 构造
     *
     * @param context context
     */
    public CustomFilePathView(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CustomFilePathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rootDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        paddingWidth = DimensionPixelUtil.dip2px(getContext(), 5);
        paddingTop = DimensionPixelUtil.dip2px(getContext(), 12);
    }

    /**
     * 设置路径
     *
     * @param path path
     */
    public void setPath(String path) {
        removeAllViews();
        createTextDrawable(path);
    }

    /**
     * 添加路径
     *
     * @param path path
     */
    public synchronized void addPath(String path) {
        int childLength = getChildCount();
        int backIndex = -1;
        for (int i = 0; i < childLength; i++) {
            if (path.equals(getChildAt(i).getTag())) {
                backIndex = i;
            }
        }
        if (backIndex == -1) {
            createTextDrawable(path);
        } else {
            for (int i = childLength - 1; i > backIndex; i--) {
                removeViewAt(i);
            }
        }
    }

    private void createTextDrawable(String path) {
        TextView view = (TextView) View.inflate(getContext(), R.layout.ac_local_file_path, null);
        String content = path;
        if (getChildCount() > 0) {
            String contentCache = (String) getChildAt(getChildCount() - 1).getTag();
            content = path.substring(contentCache.length(), path.length());
        }
        if (!TextUtils.isEmpty(rootDirPath) && rootDirPath.equals(content)) {
            view.setText(content.substring(1, content.length()));
        } else if (!TextUtils.isEmpty(content)) {
            view.setText("> " + content.substring(1, content.length()));
        }
        view.setTag(path);
        view.setPadding(paddingWidth, paddingTop, paddingWidth, paddingTop);
        view.setMaxLines(1);
        view.setEllipsize(TextUtils.TruncateAt.START);
        view.setOnClickListener(onClickListener);
        //        Drawable drawable = CompatUtils.getDrawable(getContext(),R.drawable.com_common_item_selector);
        //        view.setBackground(drawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, layoutParams);
    }


    public void setPathClickListener(PathClickListener pathClickListener) {
        this.pathClickListener = pathClickListener;
    }

    /**
     * 点击路径
     */
    public interface PathClickListener {
        /**
         * 返回
         *
         * @param path path
         */
        void clickBack(String path);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isShowComplete = true;
        int width = 0;
        for (int i = 0; i < getChildCount(); i++) {
            TextView childView = (TextView) getChildAt(i);
            childView.setMaxWidth(5000);
            childView.measure(widthMeasureSpec, heightMeasureSpec);
            width += childView.getMeasuredWidth();
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        if (getMeasuredWidth() < width) {
            isShowComplete = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int right;
        TextView childView;
        if (!isShowComplete) {
            left = getMeasuredWidth();
            for (int i = getChildCount() - 1; i >= 0; i--) {
                childView = (TextView) getChildAt(i);
                left = left - childView.getMeasuredWidth();
                right = left + childView.getMeasuredWidth();
                if (left == 0) {
                    right = 0;
                } else if (left < 0) {
                    left = 0;
                    childView.setMaxWidth(right - left);
                    childView.measure(childView.getMeasuredWidth(), getMeasuredHeight());
                }
                childView.layout(left, 0, right, getMeasuredHeight());
            }
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                childView = (TextView) getChildAt(i);
                right = left + childView.getMeasuredWidth();
                childView.layout(left, 0, right, getMeasuredHeight());
                left = right;
            }
        }
    }
}