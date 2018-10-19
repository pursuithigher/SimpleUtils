package com.dzbook.utils.hw;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.dzbook.activity.SplashActivity;
import com.dzbook.model.ModelAction;
import com.dzbook.utils.ListUtils;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 快捷方式工具类
 *
 * @author caimantang on 2018/4/12.
 */

public class ShortcutUtils {


    /**
     * 添加Shortcut入口
     *
     * @param context context
     */
    public static void addShortcut(Context context) {
        //设置压感
        List<ShortcutInfo> list = new ArrayList<>();
        //搜索
        list = addItem(context, list, ModelAction.TO_SEARCH, context.getResources().getString(R.string.str_search), R.drawable.ic_shortcut_seach);
        //推荐
        list = addItem(context, list, ModelAction.TO_BOOKSTORE, context.getResources().getString(R.string.fr_recommend_titletext), R.drawable.ic_shortcut_recommend);
        //签到
        list = addItem(context, list, ModelAction.TO_SIGN, context.getResources().getString(R.string.sign_in), R.drawable.ic_shortcut_sign);
        //继续阅读
        list = addItem(context, list, ModelAction.TO_READER, context.getResources().getString(R.string.keep_read), R.drawable.ic_shortcut_reader);
        enableShortcuts(context, list);
    }

    /**
     * 添加元素
     *
     * @param context  context
     * @param list     list
     * @param turnPage turnPage
     * @param label    label
     * @param resId    resId
     * @return List<ShortcutInfo>
     */
    public static List<ShortcutInfo> addItem(Context context, List<ShortcutInfo> list, int turnPage, @NonNull CharSequence label, @DrawableRes int resId) {
        if (ListUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        Intent intentSearch = new Intent(context, SplashActivity.class);
        intentSearch.putExtra("turnPage", turnPage);
        ShortcutInfo searchShortcutInfo = ShortcutUtils.createShortcut(context, intentSearch, turnPage + "", label, label, resId);
        if (null != searchShortcutInfo) {
            list.add(searchShortcutInfo);
        }
        return list;
    }

    /**
     * 添加压感入口
     * Shortcuts 的静态+动态总数不能超过5个。
     *
     * @param context context
     * @param list    list
     */
    public static void enableShortcuts(Context context, List<ShortcutInfo> list) {
        try {
            if (null == context || ListUtils.isEmpty(list)) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutManager manager = context.getSystemService(ShortcutManager.class);
                manager.setDynamicShortcuts(list);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 创建Shortcut
     *
     * @param context    context
     * @param intent     intent
     * @param id         id
     * @param longLabel  longLabel
     * @param shortLabel shortLabel
     * @param resId      resId
     * @return ShortcutInfo
     */
    public static ShortcutInfo createShortcut(Context context, Intent intent, String id, @NonNull CharSequence longLabel, @NonNull CharSequence shortLabel, @DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, id)
                    // 长名(优先显示长名)
                    .setLongLabel(longLabel)
                    // 短名(如果长名显示不下, 就显示短名)
                    .setShortLabel(shortLabel).setIcon(Icon.createWithResource(context, resId)).setIntent(intent.setAction(Intent.ACTION_VIEW).addCategory(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION)).build();
            return shortcutInfo;
        }
        return null;
    }
}