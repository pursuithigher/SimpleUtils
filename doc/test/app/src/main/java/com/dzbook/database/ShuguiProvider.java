package com.dzbook.database;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.BookMark;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.database.bean.BookNote;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.database.bean.PluginInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.DeviceInfoUtils;
import com.iss.db.BaseContentProvider;
import com.iss.db.DbConfig;
import com.iss.db.IssDbFactory;

/**
 * 数据库配置
 *
 * @author lizhongzhong 2013-11-23
 */
public class ShuguiProvider extends BaseContentProvider {
    /**
     * 获取 provider 使用的 authority
     *
     * @param context
     * @return
     */
    private String getAuthority(Context context) {
        try {
            // 直接读取 provider 使用的 authority
            String packName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packName, PackageManager.GET_PROVIDERS);
            for (ProviderInfo provider : pi.providers) {
                if (provider.name.equalsIgnoreCase(this.getClass().getName())) {
                    return provider.authority;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            ALog.printStackTrace(e);
        }
        // 读取失败，需要返回默认的 authority
        return "dzbook." + DeviceInfoUtils.getInstanse().getPackName();
    }

    @Override
    public void init() {
        String authority = getAuthority(getContext());
        // 数据库相关参数设置
        DbConfig.Builder builder = new DbConfig.Builder();

        builder.setName("ishugui.db").setVersion(27).setAuthority(authority);

        builder.addTatble(BookInfo.class).addTatble(CatalogInfo.class).addTatble(BookMark.class).addTatble(HttpCacheInfo.class)
                // 2017-12-05 新版的笔记功能添加这个表
                .addTatble(BookNote.class)
                //2018-01-17 3.6版本语音功能插件添加这个表
                .addTatble(PluginInfo.class)
                //2018-06-11 华为版本同步书签和笔记功能新添加
                .addTatble(BookMarkNew.class);

        DbConfig config = builder.build();

        IssDbFactory.init(getContext(), config);
    }

}
