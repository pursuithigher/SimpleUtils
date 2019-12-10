package com.network;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpsResponse {
	
	static Class<?> TAG=HttpsResponse.class;
	
	private int code=0;
	private String result=null;
	public final static String CODE="code";
	public final static String RESULT="result";
	
	private HttpsResponse(){
		
	}
	
	public HttpsResponse(int code,String result){
		this.code=code;
		this.result=result;
	}
	
	public int getCode() {
		return code;
	}
	public String getResult() {
		return result;
	}
	
	public String toJsonObject(){
		JSONObject iobject=new JSONObject();
		try {
			iobject.put(CODE, code);
			iobject.put(RESULT, result);
			return iobject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static HttpsResponse getHttpsResponse(String result){
		HttpsResponse https=new HttpsResponse();
		if(TextUtils.isEmpty(result))
			return https;
		try {
			JSONObject iobject=new JSONObject(result);
			if(iobject.has(CODE))
				https.code=iobject.getInt(CODE);
			if(iobject.has(RESULT))
				https.result=iobject.getString(RESULT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return https;
	}
}
