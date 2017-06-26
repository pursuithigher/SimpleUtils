package com.device.media;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.interf.BaseInterface;
import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-6-23.
 */

public class TakePicActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView canvas;
    private CameraDelegate delegate;
    private ImageView camera_image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_camera);
        canvas = (SurfaceView) findViewById(R.id.device_camera_canvas);
        camera_image = (ImageView) findViewById(R.id.camera_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            delegate = new CameraApi21();
        }else{
            delegate = new CameraApiLow();
        }

        canvas.getHolder().addCallback(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                delegate.initialCamear2(this,canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            delegate.closeCamera();
        }
    }

    public void takePicture(View view){
        delegate.takePicture(this, new BaseInterface<Bitmap>() {
            @Override
            public void onComplete(Bitmap result) {
                camera_image.setImageBitmap(result);
                canvas.setVisibility(View.GONE);
            }

            @Override
            public void onError(@StringRes int resId) {

            }
        });
    }
}
