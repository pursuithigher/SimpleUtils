package com.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.support.annotation.StringRes;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.interf.BaseInterface;
import com.network.HttpClient;
import com.views.simpleutils.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;

import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 17-4-20 created
 * 图片三级缓存
 * 存放与私有或者共有dir
 */
public class FileManager {
    private LruCache<String, Bitmap> mMemoryCache = null;
    private static FileManager instance = null;

    /**
     * private dir
     */
    private final static String HEADDIR = "userimage";

    /**
     * public dir
     */
    private final static String IMAGE_DIR = "images";
    private FileManager(Application context){
        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        //给LruCache分配1/8 4M
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){
            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public static FileManager getInstance(Application context) {
        if (instance == null)
        {
            synchronized (FileManager.class)
            {
                if(instance == null)
                {
                    instance = new FileManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取主目录文件夹路径
     * @param context
     * @return
     */
    private static String getHelixntFileDir(Context context)
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();
            return sdDir.getAbsolutePath() + "/"+ BuildConfig.AppNameDir;
		}
		else
		{
			return getPrivateHeadFileDir(context).getAbsolutePath();
		}
	}

    /**
     * 获取在主目录下{@link #IMAGE_DIR}目录
     * @param t
     * @return
     */
    public static File getPublicHeadDir(Context t)
	{
		File dirs = new File(getHelixntFileDir(t),IMAGE_DIR);
		if(!dirs.exists())
			dirs.mkdirs();
		return dirs;
	}
    /**
     * 获取私有文件夹路径
     * @param context
     * @return
     */
    private static File getPrivateHeadFileDir(Context context){
        File file = context.getDir(BuildConfig.AppNameDir,Context.MODE_PRIVATE);
        return file;
    }

    /**
     * 取网络路径的最后/后缀作为文件名
     * @param context context
     * @param fileName net path
     * @return File that has get rid of suffix
     */
    private static File GetPublicHeadFile(Context context,String fileName){
        String fname = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
        return new File(getPublicHeadDir(context),fname);
    }

    /**
     * 新建私有文件夹下某个文件
     * @param context
     * @param fileName
     * @return
     */
    private static File GetPrivateHeadFile(Context context,String fileName){
        String fname = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
        File file = new File(getPrivateHeadFileDir(context)+"/"+HEADDIR);
        if(!file.exists())
            file.mkdirs();
        return new File(file,fname);
    }


    /**
     * downLoad image and write to local file if file not exist
     * read local and net both in Async
     * @param icUri http head uri = NetConst.convertAddr(ipAddress) + AppSetting.PORT + HEADIMGGET;
     * @param fileName pic name
     * @param param USER_NAME
     * @param context cpntent
     * @param callback callback
     * @param isprivate true
     * @param currentThred IO thread or immediately
     */
    public void DownloadBitmap(final String icUri, final String fileName, final String param, final WeakReference<Application> context,
                               final BaseInterface<File> callback, final boolean isprivate, final boolean currentThred){
        final boolean netWorkAvailable = SimpleUtils.netWorkAvailable(context.get());
        //first request the bitmap inputStream
        Observable.create(new Observable.OnSubscribe<InputStream>() {
            @Override
            public void call(Subscriber<? super InputStream> subscriber) {
                try {
                    InputStream is = null;
                    if(!TextUtils.isEmpty(fileName)) {
                        File imagefile = isprivate ? GetPrivateHeadFile(context.get(), fileName) : FileManager.GetPublicHeadFile(context.get(), fileName);
                        final boolean isFileExist = imagefile.exists();
                        if (isFileExist) {
//                        Log.i("file","exist path :"+path);
                            is = new FileInputStream(imagefile);
                        } else if (netWorkAvailable) {
                            Log.i("file", "doesnot exist load from net");
                            Response response = HttpClient.Get_Sync(icUri, param);
                            is = response.body().byteStream();

                        }
                    }
                    subscriber.onNext(is);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).map(new Func1<InputStream,Bitmap>() {//this decode bitmap
            @Override
            public Bitmap call(InputStream inputStream) {
                if(inputStream != null)
                    return BitmapFactory.decodeStream(inputStream);
                 return null;
            }
        }).map(new Func1<Bitmap, File>() {//this write to private file dir
            @Override
            public File call(Bitmap bitmap) {
                File file = null;
                try {
                    if(context.get() == null)
                        return null;
                    if(bitmap != null) {
                        file = isprivate ? GetPrivateHeadFile(context.get(), fileName) : FileManager.GetPublicHeadFile(context.get(), fileName);
                        FileOutputStream os = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        os.flush();
                        os.close();
                        mMemoryCache.put(file.getName(),bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file;
            }
        }).subscribeOn(currentThred ? Schedulers.immediate() : Schedulers.io())
                .observeOn(currentThred ? Schedulers.immediate() : AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        //do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(callback != null)
                            callback.onComplete(null);
                    }

                    @Override
                    public void onNext(File file) {
                        if(callback != null)
                            callback.onComplete(file);
                    }
                });
    }

    /**
     * save Bitmap to special file
     * @param context
     * @param bitmap
     * @param name
     * @param isprivate
     * @return
     */
    private String encodeBitmapToFile(Context context,Bitmap bitmap,String name,boolean isprivate){
        if(name == null)
            return null;

        if(name.lastIndexOf(".")==-1)
        {
            name+=".png";
        }
        File file = new File(isprivate ? getPrivateHeadFileDir(context) : getPublicHeadDir(context),name);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            bitmap.recycle();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

//    public void saveFileToPrivate(final File f1,String file2,final boolean isprivate ,final BaseInterface<String> interf){
//        if(file2 == null)
//            return ;
//        if(file2.lastIndexOf(".")==-1)
//        {
//            file2+=".png";
//        }
//        Context context = com.helixnt.Application.getInstance();
//        final File f2 = new File(isprivate ? getPrivateHeadFileDir(context) : getBeautyFileDir(context),file2);
//        Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                boolean issucceed = FileCopyToPrivate(f1,f2);
//                if(issucceed)
//                    subscriber.onNext(f2.getAbsolutePath());
//                else
//                    subscriber.onNext(null);
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String filepath) {
//                        interf.onComplete(filepath);
//                    }
//                });
//    }

    private static boolean FileCopyToPrivate(File from, File to){
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(from);
            fo = new FileOutputStream(to);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
            fi.close();
            in.close();
            fo.close();
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Bitmap loadBitmap(int width , String filePath){
        if(filePath == null)
            return null;
        Bitmap map = getImageFromCache(filePath);
        if(map == null)
        {
            map= loadImageFromFile(width, filePath);
        }else if(map.isRecycled()){
            mMemoryCache.remove(new File(filePath).getName());
            map= loadImageFromFile(width, filePath);
        }
        return map;
    }

    private Bitmap getImageFromCache(String filePath){
        if(filePath == null)
            return null;
        else
            return mMemoryCache.get(new File(filePath).getName());
    }

    /**
     * scale the image to fit the imageView
     * @param width if >60 then use right scale
     * @param filePath path
     * @return the height that ImageView can take
     */
    private Bitmap loadImageFromFile(int width , String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap map = null;
        if(width > 60) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            float imageWidth = options.outWidth * 1.0f;
            options.outHeight = (int) (width * options.outHeight / imageWidth);
            options.outWidth = width;
        }
        options.inJustDecodeBounds = false;
        map = BitmapFactory.decodeFile(filePath,options);
        if(filePath != null && map != null) {
            mMemoryCache.put(new File(filePath).getName(), map);
        }
        return map;
    }

    /**
     * 裁剪图片，使得寬高比相同，大小不等同，只保证寬高比相同
     * @param rawBitmap input bitmap
     * @param towidth width you want
     * @param toheight height you want
     * @return the bitmap hold the scale that width/height = towidth/toheight it doesnot use towidth,toheight size
     */
    public static Bitmap createRegularBitmap(Bitmap rawBitmap,int towidth,int toheight){
        if(rawBitmap == null)
            return null;
        int width = rawBitmap.getWidth();
        int height = rawBitmap.getHeight();
        float bitscale = height*1f/width;
        float toscale = toheight*1f/towidth;

        int canvasWidth = width;
        int canvasHeight = height;

        if(bitscale > toscale)
        {
            canvasHeight = (int) (toscale*width);
        }
        if(bitscale < toscale)
        {
            canvasWidth = (int) (height / toscale);
        }
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawBitmap(rawBitmap, -(width - canvasWidth)/2 , -(height - canvasHeight)/2, paint);
        rawBitmap.recycle();
        return target;
    }


    public static void test(final Application context,String fileName,String uri){
        //1:从cache和本地读取
        Bitmap map = FileManager.getInstance(context).loadBitmap(-1,fileName);

        String requestParam = null;
        if(map == null)
        {
            FileManager.getInstance(context).DownloadBitmap(uri, fileName, requestParam,
                    new WeakReference<Application>(context), new BaseInterface<File>() {
                        @Override
                        public void onComplete(File result) {
                            //callback that get Bitmap file,the bitmap has been added in LruCache
                            Bitmap map2 = FileManager.getInstance(context).loadImageFromFile(-1,result.getName());
                        }
                        @Override
                        public void onError(@StringRes int resId) {

                        }
                    },true,false);
        }
    }
}
