package com.dzbook.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dzbook.bean.BookMarkSyncInfo;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.SpUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Service
 */
public class SyncBookMarkService extends IntentService {
    private static final String TAG = "SyncBookMarkService";

    /**
     * 构造
     */
    public SyncBookMarkService() {
        super(TAG);
    }

    /**
     * 跳转
     *
     * @param context context
     */
    public static void launch(Context context) {
        launch(context, false);
    }

    /**
     * 跳转
     *
     * @param context   context
     * @param forceSync forceSync
     */
    public static void launch(Context context, boolean forceSync) {
        Intent intent = new Intent(context, SyncBookMarkService.class);
        intent.putExtra("forceSync", forceSync);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean forceSync = intent.getBooleanExtra("forceSync", false);

        //step1:获取当前userId，如果userId为空，终止
        String userId = SpUtil.getinstance(getApplicationContext()).getUserID();
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        //step2:将未绑定user的书签和笔记绑定到当前用户
        BookMarkNew.upateMarkByUserId(getApplicationContext(), userId);

        //step3:取出当前userId下未同步的书签和笔记
        String upJson = "";
        ArrayList<BookMarkNew> unSyncList = BookMarkNew.getUnSyncMark(getApplicationContext(), userId);
        if (unSyncList != null && unSyncList.size() > 0) {
            JSONArray array = new JSONArray();
            for (BookMarkNew mark : unSyncList) {
                array.put(mark.toJSON());
            }
            upJson = array.toString();
        }
        if (!forceSync && TextUtils.isEmpty(upJson)) {
            return;
        }


        try {
            //step4:上传取出的书签和笔记至服务器
            String syncTime = SpUtil.getinstance(getApplicationContext()).getBookMarkSyncTime(userId);

            BookMarkSyncInfo syncInfo = HwRequestLib.getInstance().syncMark(userId, syncTime, upJson);
            if (syncInfo == null || !syncInfo.isSuccess() || TextUtils.isEmpty(syncInfo.time)) {
                return;
            }

            //step5:更新服务器下放的书签和笔记，并标记已同步
            for (BookMarkNew bookMarkNew : syncInfo.markList) {
                bookMarkNew.userId = userId;
                if (bookMarkNew.operate == BookMarkNew.OPERATE_DEL) {
                    BookMarkNew.deleteMark(getApplicationContext(), bookMarkNew, true);
                } else {
                    bookMarkNew.operate = BookMarkNew.OPERATE_NORMAL;
                    BookMarkNew.addMark(getApplicationContext(), bookMarkNew);
                }
            }

            //step6:保存同步时间
            SpUtil.getinstance(getApplicationContext()).setBookMarkSyncTime(userId, syncInfo.time);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
