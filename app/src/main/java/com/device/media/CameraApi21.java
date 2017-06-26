package com.device.media;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.interf.BaseInterface;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by qzzhu on 17-6-22.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraApi21 implements CameraDelegate{
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private CameraDevice camera2;
    private CameraCaptureSession mCameraCaptureSession;
    private SurfaceHolder mSurfaceHolder;
    private ImageReader imageReader;
    private Handler mainHandler ;
    private Handler childHandler;

    public void initialCamear2(AppCompatActivity context,SurfaceView mSurface) throws Exception {
        //获取摄像头管理
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        mainHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());

        this.mSurfaceHolder = mSurface.getHolder();

        Display display = context.getWindowManager().getDefaultDisplay();
        imageReader = ImageReader.newInstance(display.getWidth(),display.getHeight(), ImageFormat.JPEG,1);

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraManager.openCamera(String.valueOf(CameraCharacteristics.LENS_FACING_FRONT), stateCallback, mainHandler);
        }else{
            throw new Exception("Camera Permission Forbidden");
        }
    }

    /**
     * 摄像头监听
     */
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //start preview
            camera2 = camera;
            try {
                takePreview();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            //stop camera and stop preview
            closeCamera();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            //error happened
        }
    };

    public void closeCamera(){
        if(null != camera2)
        {
            camera2.close();
            camera2 = null;
        }
    }

    /**
     * 开始预览
     * @throws Exception
     */
    private void takePreview()throws Exception{
        final CaptureRequest.Builder previewBuilder = camera2.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        previewBuilder.addTarget(mSurfaceHolder.getSurface()); //与UI绑定

        //创建一个Session用来将APP与hardware链接
        camera2.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                if (null == camera2)
                    return;
                // 当摄像头已经准备好时，开始显示预览
                mCameraCaptureSession = cameraCaptureSession;
                try {
                    // 自动对焦
                    previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    // 打开闪光灯
                    //previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    // 显示预览
                    CaptureRequest previewRequest = previewBuilder.build();
                    mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {

            }
        },mainHandler);
    }

    /**
     * 拍照
     */
    public void takePicture(AppCompatActivity context,final BaseInterface<Bitmap> callback) {
        if (camera2 == null)
            return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = camera2.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(imageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            //captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();

            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() { //可以在这里处理拍照得到的临时照片 例如，写入本地
                @Override
                public void onImageAvailable(ImageReader reader) {
                    camera2.close();
                    // 拿到拍照照片数据
                    Image image = reader.acquireNextImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);//由缓冲区存入字节数组
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmap != null) {
                        callback.onComplete(bitmap);
                    }
                }
            },mainHandler);

            mCameraCaptureSession.capture(mCaptureRequest, null, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
