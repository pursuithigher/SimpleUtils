package com.dzbook.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.view.text.HwStyleSpan;
import com.ishugui.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * View工具类
 *
 * @author gavin
 */
public class ViewUtils {

    protected static long lastClickTime = 0;
    protected static View view;
    private static HashMap<String, GradientDrawable> gradientDrawables;

    /**
     * 搜索高亮
     *
     * @param context        context
     * @param mTextViewTitle mTextViewTitle
     * @param highKey        highKey
     */
    public static void highlightTextSearch(Context context, TextView mTextViewTitle, String highKey) {
        int color = CompatUtils.getColor(context, R.color.color_100_CD2325);
        ViewUtils.highlightText(mTextViewTitle, highKey, color);
    }

    /**
     * highlight
     *
     * @param textView textView
     * @param color    color
     * @param key      key
     */
    public static void highlightText(TextView textView, String key, int color) {
        try {
            if (TextUtils.isEmpty(key) || null == textView) {
                return;
            }
            SpannableString s = new SpannableString(textView.getText());
            HwStyleSpan hwStyleSpan = new HwStyleSpan(HwStyleSpan.MEDIUM);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);

            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(hwStyleSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(foregroundColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(s);
        } catch (Exception ignore) {
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context context
     * @param tag     tag
     */
    public static void hideInputKeyboard(final Context context, String tag) {
        final Activity activity = (Activity) context;
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager mInputKeyBoard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (activity.getCurrentFocus() != null) {
                        assert mInputKeyBoard != null;
                        mInputKeyBoard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ALog.e(tag, e.getMessage());
        }
    }


    private static GradientDrawable getGradientDrawable(int pRoundRadius, String pColor) {
        int fillColor;
        try {
            if (!TextUtils.isEmpty(pColor)) {
                fillColor = Color.parseColor(pColor);
            } else {
                fillColor = Color.parseColor("#CD2325");
            }
        } catch (IllegalArgumentException exception) {
            fillColor = Color.parseColor("#CD2325");
        }
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(pRoundRadius);
        return gd;
    }

    /**
     * 获取GradientDrawable
     *
     * @param key          key
     * @param pRoundRadius pRoundRadius
     * @return GradientDrawable
     */
    public static GradientDrawable getGradientDrawable(String key, int pRoundRadius) {
        if (gradientDrawables == null) {
            gradientDrawables = new HashMap<>();
        }
        if (gradientDrawables.containsKey(key)) {
            return gradientDrawables.get(key);
        } else {
            GradientDrawable drawable = getGradientDrawable(pRoundRadius, key);
            if (drawable != null) {
                gradientDrawables.put(key, drawable);
            }
            return drawable;
        }
    }
}
