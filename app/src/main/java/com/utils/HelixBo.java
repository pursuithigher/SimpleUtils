//package com.utils;
//
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//
//import android.content.Context;
//import android.os.Environment;
//import android.text.TextUtils;
//
//public class HelixBo
//{
//	private final static Class<HelixBo> TAG = HelixBo.class;
//
//	/**
//	 * 读SD中的文件
//	 * @return
//	 */
//	public static String readFile(String dir)
//	{
//		String res = "";
//		try
//		{
//			FileInputStream fin = new FileInputStream(HelixBo.getFilename(dir));
//			int length = fin.available();
//			byte[] buffer = new byte[length];
//			fin.read(buffer);
//			res = new String(buffer,"utf-8");
//			fin.close();
//		}
//		catch (Exception e)
//		{
//			//MyLog.e(TAG, e);
//		}
//		return res;
//	}
//
//	/**
//	 *
//	 * @return
//	 */
//	public static String getFilename()
//	{
//		MyLog.d(TAG, "Enter getFilename()");
//		File sdDir = null;
//		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//		if (sdCardExist)
//		{
//			sdDir = Environment.getExternalStorageDirectory();
//			MyLog.d(TAG, "exist sdCard" );
//		}
//		MyLog.d(TAG, "Leave getFilename()");
//		if (TextUtils.isEmpty())
//		{
//			File file=new File(sdDir.getAbsolutePath() + "/helixnt");
//			if(!file.exists())
//				file.mkdirs();
//			return file.getAbsolutePath()+"/helixnt.json";
//		} else
//		{
//			File file=new File(AppSetting.ROOT_PATH + "/helixnt");
//			if(!file.exists())
//				file.mkdirs();
//			return file.getAbsolutePath()+"/helixnt.json";
//		}
//	}
//
//	public static String getHelixntFileDir(Context context)
//	{
//		MyLog.d(TAG, "Enter getFilename()");
//		File sdDir = null;
//		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//		if (sdCardExist)
//		{
//			sdDir = Environment.getExternalStorageDirectory();
//			MyLog.d(TAG, "exist sdCard" );
//		}
//		MyLog.d(TAG, "Leave getFilename()");
//		if (StringUtil.validate(sdDir))
//		{
//			return sdDir.getAbsolutePath() + "/helixnt";
//		} else
//		{
//			return context.getFilesDir().getPath() + "/helixnt";
//		}
//	}
//
//	public final static String PrivateDir_Beauty = "headers";
//	public final static String PrivateDir_Wav = "wavs";
//	public static File getWavFileDir(Context t)
//	{
//		String dir = getHelixntFileDir(t)+"/"+PrivateDir_Wav;
//
//		File dirs = new File(dir);
//		if(!dirs.exists())
//			dirs.mkdirs();
//		return dirs;
//	}
//
//	public static File getHeadimgFileDir(Context t)
//	{
//		File dirs = new File(getHelixntFileDir(t),PrivateDir_Beauty);
//		if(!dirs.exists())
//			dirs.mkdirs();
//		return dirs;
//	}
//
//	public static String getFilename(String dirs)
//	{
//		if(dirs == null)
//			return getFilename();
//		MyLog.d(TAG, "Enter getFilename()");
//		File sdDir = null;
//		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//		if (sdCardExist)
//		{
//			sdDir = Environment.getExternalStorageDirectory();
//			MyLog.d(TAG, "exist sdCard" );
//		}
//		MyLog.d(TAG, "Leave getFilename()");
//		if (StringUtil.validate(sdDir))
//		{
//			File file=new File(sdDir.getAbsolutePath() + "/helixnt/"+dirs);
//			if(!file.exists())
//				file.mkdirs();
//			return file.getAbsolutePath()+"/helixnt.json";
//		} else
//		{
//			File file=new File(AppSetting.ROOT_PATH + "/helixnt/"+dirs);
//			if(!file.exists())
//				file.mkdirs();
//			return file.getAbsolutePath()+"/helixnt.json";
//		}
//	}
//
//	/**
//	 * helixnt.json format and wirte it to file [helixnt package at SD card]
//	 * @param helixjson unformat helixnt.json
//	 */
//	public static void writeFile(String helixjson,String dir)
//	{
//		MyLog.d(TAG, "Enter writeFile()");
//		if (StringUtil.isEmpty(helixjson))
//		{
//			return;
//		}
//
//
//		JsonFormatTool jsonformattool = new JsonFormatTool();
//		//helixnt.json format because of lots of mixed strings
//		String formatJson = jsonformattool.formatJson(helixjson);
//
//		File file = new File(HelixBo.getFilename(dir));
//
//		if(!file.exists())
//		{
//			try {
//				File parents = file.getParentFile();
//				if(!parents.exists()) {
//					boolean isParents = parents.mkdirs();
//					if(isParents)
//					{
//						file.createNewFile();
//					}
//				}
//				else{
//					file.createNewFile();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		DataOutputStream oStream = null;
//		try
//		{
//			oStream = new DataOutputStream(new FileOutputStream(file));
//			oStream.write(formatJson.getBytes());
//			MyLog.d(TAG, "write to file" );
//			oStream.flush();
//		} catch (IOException e)
//		{
//			MyLog.e(TAG, e);
//		} finally
//		{
//			if (oStream != null)
//			{
//				try
//				{
//					MyLog.d(TAG, "close file" );
//					oStream.close();
//				} catch (IOException e)
//				{
//					MyLog.e(TAG, e);
//				}
//			}
//		}
//		MyLog.d(TAG, "Leave writeFile()");
//	}
//
//	public static void deleteFile(Context context,String dir){
//		File helixnt=new File(getFilename(dir));
//		if(helixnt.exists())
//		{
//			if(helixnt.getParentFile().isDirectory())
//			{
//				File[] files=helixnt.listFiles();
//				if(files != null)
//				{
//					for(File item:files)
//					{
//						if(item.getName().contains("App"))
//						{
//							continue ;
//						}
//						if(item.getName().contains("helixnt"))
//							item.delete();
//					}
//				}
//			}
//			helixnt.delete();
//		}
//	}
//}
