package com.db.provider;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil
{

	private static PreferencesUtil instance;

	private SharedPreferences pref;

	private final static String DISTURB_FROM = "disturbfrom";
	private final static String DISTURB_TO = "disturbto";

	private final String MSGSOUND = "msgsound";
	private final String MSGSHAKE = "msgshake";

	public int getMsgDisturbFrom(){
		return pref.getInt(DISTURB_FROM, -1);
	}
	public int getMsgDisturbTo(){
		return pref.getInt(DISTURB_TO, -1);
	}

	public void setMsgSound(boolean on){
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(MSGSOUND, on);
		editor.apply();
	}
	public boolean getMsgSound(){
		return pref.getBoolean(MSGSOUND, true);
	}

	public void setMsgShake(boolean on){
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(MSGSHAKE, on);
		editor.apply();
	}
	public boolean getMsgShake(){
		return pref.getBoolean(MSGSHAKE, true);
	}

	public void setMsgDisturb(int from,int to){
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(DISTURB_FROM, from);
		editor.putInt(DISTURB_TO, to);
		editor.apply();
	}


	private PreferencesUtil(Context aContext)
	{
		pref = aContext.getSharedPreferences("helixnt", Context.MODE_PRIVATE);
	}

	public static PreferencesUtil getIntance(Context context)
	{
		if (instance == null)
		{
			instance = new PreferencesUtil(context);
		}

		return instance;
	}


}
