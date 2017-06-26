package com.device.media;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import com.interf.BaseInterface;

/**
 * Created by qzzhu on 17-6-26.
 */

public interface CameraDelegate {
    void initialCamear2(AppCompatActivity context, SurfaceView mSurface) throws Exception;
    void closeCamera();
    void takePicture(AppCompatActivity context,final BaseInterface<Bitmap> callback);
}
