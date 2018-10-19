package com.dzbook.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.DisplayMetrics;

import com.dzbook.AppConst;
import com.dzbook.lib.utils.ALog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ImageUtils
 *
 * @author gavin
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final float COLOR_MAX_SIZE = 255f;
    private static final float COLOR_80_SIZE = 80f;

    private ImageUtils() {
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param resId       resId
     * @param isBlackMode isBlackMode
     * @return bitmap
     * @see </br>修改为</br>
     * imageButton_fav.setImageBitmap(BitmapUtils.readBitMap(this,
     * R.drawable.guide_fav_1));
     */
    public static Bitmap readBitmap(int resId, boolean isBlackMode) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = isBlackMode ? 2 : 1;
        Bitmap decodeBitmap;
        try {
            InputStream is = AppConst.getApp().getResources().openRawResource(resId);
            decodeBitmap = BitmapFactory.decodeStream(is, null, opt);
            return decodeBitmap;
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return null;
    }

    /**
     * 保存图片到指定目录
     *
     * @param bm   bm
     * @param path path
     * @return boolean 是否存储成功
     */
    public static boolean saveBitmap(Bitmap bm, String path) {
        ALog.i(TAG, "保存图片");
        File f = new File(path);
        if (f.exists()) {
            if (f.delete()) {
                ALog.dWz("delete success " + f);
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            ALog.i(TAG, "已经保存");
            return true;
        } catch (Exception e) {
            ALog.printExceptionWz(e);
            return false;
        }
    }

    /**
     * 压缩图片到指定大小
     *
     * @param context       Activity
     * @param bitmap        bitmap
     * @param specifiedSize 单位 K
     * @param isRecycler    isRecycler
     * @return byte[]
     */
    public static byte[] compressBitmap(Activity context, Bitmap bitmap, int specifiedSize, boolean isRecycler) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float hh = dm.heightPixels;
        float ww = dm.widthPixels;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        System.out.println(baos.toByteArray().length);
        //不能无限制的压缩图片，所以添加了质量大于10的判断
        while (baos.toByteArray().length > specifiedSize * 1024 && quality > 10) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 5;
            ALog.iWz("baos.toByteArray().length:" + baos.toByteArray().length);
        }
        try {
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
                if (isRecycler) {
                    bitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 颜色合成。
     *
     * @param colorBg    被覆盖层颜色
     * @param colorCover 覆盖层颜色
     * @return int
     */
    public static int mixColor(int colorBg, int colorCover) {
        int bgA = Color.alpha(colorBg);
        int coverA = (colorCover >>> 24) & 0xff;
        float bgAlpha = bgA / COLOR_MAX_SIZE;
        float coverAlpha = coverA / COLOR_MAX_SIZE;

        int dstA = (int) (coverA + bgA * (1 - coverAlpha));
        int dstR = (int) (Color.red(colorCover) * coverAlpha + Color.red(colorBg) * bgAlpha * (1 - coverAlpha));
        int dstG = (int) (Color.green(colorCover) * coverAlpha + Color.green(colorBg) * bgAlpha * (1 - coverAlpha));
        int dstB = (int) (Color.blue(colorCover) * coverAlpha + Color.blue(colorBg) * bgAlpha * (1 - coverAlpha));
        return Color.argb(dstA, dstR, dstG, dstB);
    }


    /**
     * BlueFilterColor
     *
     * @return int
     */
    @ColorInt
    public static int getBlueFilterColor() {
        // 10<realFilter<80
        int blueFilter = 40;
        int a = (int) (blueFilter / COLOR_80_SIZE * 130);
        int r = (int) (200 - (blueFilter / COLOR_80_SIZE) * 190);
        int g = (int) (180 - (blueFilter / COLOR_80_SIZE) * 170);
        int b = (int) (60 - blueFilter / COLOR_80_SIZE * 60);
        return Color.argb(a, r, g, b);
    }
}
