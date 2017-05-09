package com.data;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by qzzhu on 17-5-9.
 *
 */
public class FileUtils {
    private static boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        boolean ismount = Environment.MEDIA_MOUNTED.equals(state);
        if(!ismount)
            Log.e("error","sd caed not available");
        return ismount;
    }

    /**
     * internal private file dir
     * used as getFileDir or openFile**Stream
     * @param context
     * @param parent
     * @return
     */
    public static File getPrivateFileDir(Context context,String parent){
        File result = null;
        File privateRoot = context.getFilesDir();
        if (!TextUtils.isEmpty(parent))
        {
            result = new File(privateRoot.getAbsolutePath()+"/"+parent);
            if(!result.exists()){
                result.mkdirs();
            }
        }else{
            result = privateRoot;
        }
        return result;
    }

    /**
     * external file dir can be visible or invisible
     * @param context
     * @param parent
     * @param isExternalPrivate if false then use context.getExternalFilesDir invisibleby others return root dir
     * @return null if sd card not available
     */
    public static File getExternalFileDir(Context context,String parent,boolean isExternalPrivate){
        boolean checked = isExternalStorageAvailable();
        if(checked){
            File dir ;
            File result = null;
            if(isExternalPrivate)
            {
                dir = context.getExternalFilesDir(null);
            }else{
                dir = Environment.getExternalStorageDirectory();
            }
            if (!TextUtils.isEmpty(parent))
            {
                result = new File(dir.getAbsolutePath()+"/"+parent);
                if(!result.exists()){
                    result.mkdirs();
                }
            }
            return result;
        }
        return null;
    }
}
