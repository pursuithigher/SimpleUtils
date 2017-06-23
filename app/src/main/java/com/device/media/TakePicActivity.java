package com.device.media;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-6-23.
 */

public class TakePicActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView canvas;
    private CameraDelegate delegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_camera);
        canvas = (SurfaceView) findViewById(R.id.device_camera_canvas);
        delegate = new CameraDelegate();
        canvas.getHolder().addCallback(this);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                delegate.initialCamear2(this,canvas.getHolder());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

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
}
