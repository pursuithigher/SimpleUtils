package com.device.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.interf.BaseInterface;

import java.io.IOException;

/**
 * Created by qzzhu on 17-6-26.
 */
public class CameraApiLow implements CameraDelegate {

    private Camera mCamera;

    @Override
    public void initialCamear2(AppCompatActivity context, SurfaceView mSurface) throws Exception {
        mSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = Camera.open();//默认开启后置
        mCamera.setDisplayOrientation(90);//摄像头进行旋转90°

        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                //设置预览照片的大小
                parameters.setPreviewFpsRange(mSurface.getWidth(), mSurface.getHeight());
                //设置相机预览照片帧数
                parameters.setPreviewFpsRange(4, 10);
                //设置图片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                //设置图片的质量
                parameters.set("jpeg-quality", 90);
                //设置照片的大小
                parameters.setPictureSize(mSurface.getWidth(), mSurface.getHeight());
                //通过SurfaceView显示预览
                mCamera.setPreviewDisplay(mSurface.getHolder());
                //开始预览
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    private BaseInterface<Bitmap> callback;
    @Override
    public void takePicture(AppCompatActivity context, BaseInterface<Bitmap> callback) {
        if (mCamera == null)
            return;

        this.callback = callback;
        //自动对焦后拍照
        mCamera.autoFocus(autoFocusCallback);
    }

    /**
     * 自动对焦 对焦成功后 就进行拍照
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {//对焦成功

                camera.takePicture(new Camera.ShutterCallback() {//按下快门
                    @Override
                    public void onShutter() {
                        //按下快门瞬间的操作
                    }
                }, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {//是否保存原始图片的信息

                    }
                }, pictureCallback);
            }
        }
    };

    /**
     * 获取图片
     */
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            final Bitmap resource = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (resource == null) {
                //Toast.makeText(CameraActivity.this, "拍照失败", Toast.LENGTH_SHORT).show();
            }
            final Matrix matrix = new Matrix();
            matrix.setRotate(90);
            final Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
            if (bitmap != null && callback != null) {
                mCamera.stopPreview();
                callback.onComplete(bitmap);
            }
        }
    };
}
