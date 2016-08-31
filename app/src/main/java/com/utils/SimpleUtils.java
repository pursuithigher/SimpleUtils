package com.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleUtils {
	private static long lastClickTime;
	
	final static Class<?> TAG=SimpleUtils.class;

	/** 1
	 * 0.8s之内点击无效
	 * fast click in 0.8s can be ignored
	 * @return
	 */
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();   
        if ( time - lastClickTime < 800) {
            return true;   
        }   
        lastClickTime = time;   
        return false;   
    }
    
    /** 2
	 * 是否是电话号码
     * judge whether is mobile phone
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {  
		/*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 
        联通：130、131、132、152、155、156、185、186 
        电信：133、153、180、189、（1349卫通） 
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
        */
		String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
	}


 	public final static String pattern="[a-z0-9A-Z_\u4e00-\u9fa5\u0020]+";
 	/** 3
	 * 是否是字母，数字，英文
 	 * whether args matches EN,Number,Characters
 	 * @param args
 	 * @return
 	 */
 	public static boolean isVaild(String args){
 		return args.matches(pattern);
 	}

	/** 4
	 * 是否是一串数字
	 * @param args
	 * @return
     */
 	public static boolean isNumber(String args){
 		String numpattern="[0-9]+";
 		return args.matches(numpattern);
 	}
 	
 	public final static String patternAlpha="[a-zA-Z]";

	/** 5
	 * 是否是一串字母
	 * @param args
	 * @return
     */
 	public static boolean isAlpha(String args){
 		return args.matches(patternAlpha);
 	}


	/** 6
	 * 是否是IP地址
	 * check whether addr is a valid ip address
	 * @param addr param
	 * @return
	 */
	public static boolean isIP(String addr)
	{
		if (addr.length() < 7 || addr.length() > 15 || "".equals(addr))
		{
			return false;
		}
		String rexp = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(addr);
		boolean ipAddress = mat.matches();
		return ipAddress ;
	}

	/** 7
	 * 是否拥有某权限
	 * @param context
	 * @param permissionName
	 * @return
	 */
	public static boolean CheckPermission(Context context,String permissionName){
		PackageManager imanager=context.getPackageManager();
		return PackageManager.PERMISSION_GRANTED == imanager.checkPermission(
				permissionName, context.getPackageName());
	}

	/** 8
	 * 将Cursor中的游标中某项取得转换成List
	 * @param c Cursor Hold data
	 * @param typeclass	Class
	 * @return
	 */
	public static ArrayList<?> getCursorFieldList(Cursor c, Class<?> typeclass, String key) throws Exception{
		if(c == null)
		  throw new NullPointerException("getCursorFieldList Cursor == null");
		int index = c.getColumnIndex(key);
		if(typeclass == String.class)
		{
			ArrayList<String> datas = new ArrayList<String>();
			
			if(c.moveToFirst())
			{
				while(!c.isAfterLast())
				{
					datas.add(c.getString(index));
					c.moveToNext();
				}
			}
			return datas;
		}
		else if(typeclass == Long.class){
			ArrayList<Long> datas = new ArrayList<Long>();
			if(c.moveToFirst())
			{
				while(!c.isAfterLast())
				{
					datas.add(c.getLong(index));
					c.moveToNext();
				}
			}
			return datas;
		}else if(typeclass == Integer.class){
			ArrayList<Integer> datas = new ArrayList<Integer>();
			if(c.moveToFirst())
			{
				while(!c.isAfterLast())
				{
					datas.add(c.getInt(index));
					c.moveToNext();
				}
			}
			return datas;
		}else{
			throw new ClassCastException("");
		}
	}

	/** 9
	 * 隐藏软件盘
	 * @param t
	 * @param view
     */
	public static void HideInputKeyBorad(Context t,EditText view){
		InputMethodManager imm = (InputMethodManager)t.getSystemService(Context.INPUT_METHOD_SERVICE);  
		boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
		if(isOpen)
		{
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/** 10
	 * 判断程序是否在前台
	 * 需要GET_TASK的permission
	 * check whether app in background
	 * @param context
	 * @return
     */
	public static boolean isBackGround(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
	
}
