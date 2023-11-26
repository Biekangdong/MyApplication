package com.xixia.aiimageupload.opcv;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xixia.aiimageupload.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName CameraActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/4/26 12:11
 * @Version 1.0
 * @UpdateDate 2023/4/26 12:11
 * @UpdateRemark 更新说明
 */
public class FaceCameraActivity extends Activity {
    private FrameLayout flContent;
    private TextView tvTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_camera);
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        tvTakePicture = (TextView) findViewById(R.id.tv_take_picture);

        tvTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        //先判断是否打开相机权限
        checkCameraPermission();
    }

    /**
     * 创建预览画面
     */
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;

    public void createSurfaceView() {
        //创建预览
        surfaceView = new SurfaceView(this);
        flContent.removeAllViews();
        flContent.addView(surfaceView);
        //获取预览的管理器
        mHolder = surfaceView.getHolder();
        //监听预览状态
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                //预览控件创建成功的时候，打开相机并预览
                openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                //预览控件销毁的时候，释放相机资源
                releaseCamera();
            }
        });
    }

    //权限请求
    public final int REQUEST_CAMERA_PERMISSION = 1;
    private String cameraPermission = Manifest.permission.CAMERA;

    private void checkCameraPermission() {
        //检查是否有相机权限
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            //没权限，请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            //有权限
            createSurfaceView();
        }
    }

    //权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意权限
                    createSurfaceView();
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    Toast.makeText(this, "拒绝权限，请去设置里面手动开启权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private Camera mCamera;
    private Camera.CameraInfo cameraInfo;

    /**
     * 打开相机
     * Camera.CameraInfo.CAMERA_FACING_FRONT前置
     * Camera.CameraInfo.CAMERA_FACING_BACK后置
     *
     * @param cameraIndex 摄像头的方位
     */
    public void openCamera(int cameraIndex) {
        try {
            //先释放相机资源
            releaseCamera();
            //获取相机信息
            if (cameraInfo == null) {
                cameraInfo = new Camera.CameraInfo();
            }
            //获取相机个数
            int cameraCount = Camera.getNumberOfCameras();
            //由于不知道第几个是前置摄像头，遍历获取前置摄像头
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);

                if (cameraInfo.facing == cameraIndex) {
                    mCamera = Camera.open(camIdx);
                    break;
                }
            }

            //开启预览
            startPreview();
        } catch (Exception e) {
            //获取相机异常
            mCamera = null;
        }
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        try {
            //获取屏幕宽高,预览尺寸默认为屏幕的屏幕
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            int shortSize = display.getWidth();
            int longSize = display.getHeight();
            //设置相机参数
            initPreviewParams(shortSize, longSize);
            //设置相机方向
            adjustCameraOrientation();
            //预览方式一，没缓冲区，会频繁GC
            //mCamera.setPreviewCallback(previewCallback);
            //绑定预览视图
            mCamera.setPreviewDisplay(mHolder);
            //设置缓冲区
            mCamera.addCallbackBuffer(new byte[shortSize * longSize * 3 / 2]);
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
            //开始预览
            mCamera.startPreview();
        } catch (IOException e) {

        }
    }


    /**
     * 预览数据监听
     */
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (data != null) {
                //获取预览分辨率
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();
                //拿到字节数组，可以生成图片，也可以解析数据(比如二维码扫描，人脸识别)
                //................................

//                //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
//                Image source = new Image(size.width, size.height, "Y800");
//                //图片旋转了90度，将扫描框的TOP作为left裁剪
//                source.setData(data);//填充数据
//                //解码，返回值为0代表失败，>0表示成功
//                int dataResult = mImageScanner.scanImage(source);
            }

            //不管有没有数据，重新设置缓冲区，避免频繁GC
            camera.addCallbackBuffer(data);
        }
    };

    private long startTime = 0;
    private class Base64AsynTask extends AsyncTask<Void, Void, String> {
        byte[] data;
        int width;
        int height;

        public Base64AsynTask(byte[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = System.currentTimeMillis();
        }


        @Override
        protected String doInBackground(Void... voids) {
            //旋转
//            byte[] jpegDataResult = yuv_rotate90(data, width,height );
//            String base64String = Base64.encodeToString(jpegDataResult, Base64.DEFAULT);


            //转NV21
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
            byte[] jpegData = baos.toByteArray();
            //String base64String = Base64.encodeToString(jpegData, Base64.DEFAULT);

//            //转bitmap
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
//            //旋转
//            Matrix matrix = new Matrix();
//            matrix.postScale(0.8f, 0.8f);
//            matrix.setRotate(-90);
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//
//


//            //转base64
//            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOS);
//            String base64String = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
//
//            // 释放资源
//            bitmap.recycle();

            String filePath=saveToImage(jpegData);
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            long currentTime = System.currentTimeMillis();
            long bettwen = currentTime - startTime;
            Log.e("HHH", "onPostExecute: " + bettwen);
            if (!TextUtils.isEmpty(s)) {

            }else {

            }
        }
    }


    //保存bitmap到本地
    public String saveToImage(byte[] imageData) {
        FileOutputStream fos;
        try {
            // SD卡根目录
            File dir = getExternalFilesDir("print");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File picFile = new File(dir, "bitmap.jpg");
            if (picFile.exists()) {
                picFile.delete();
            }
            fos = new FileOutputStream(picFile);
            fos.write(imageData);
            fos.flush();
            fos.close();
            return picFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置相机参数
     */
    private void initPreviewParams(int shortSize, int longSize) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            //获取手机支持的尺寸
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            //获取合适的预览尺寸，保证不变形
            Camera.Size bestSize = getBestSize(shortSize, longSize, sizes);
            //设置预览大小
            parameters.setPreviewSize(bestSize.width, bestSize.height);
            //设置图片大小，拍照
            parameters.setPictureSize(bestSize.width, bestSize.height);
            //设置格式,所有的相机都支持 NV21格式
            parameters.setPreviewFormat(ImageFormat.NV21);
            //设置聚焦，如果拍照就设置持续对焦FOCUS_MODE_CONTINUOUS_PICTURE，其它可以自动对焦FOCUS_MODE_AUTO
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            mCamera.setParameters(parameters);
        }
    }

    /**
     * 获取预览最佳尺寸
     */
    private Camera.Size getBestSize(int shortSize, int longSize, List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        float uiRatio = (float) longSize / shortSize;
        float minRatio = uiRatio;
        for (Camera.Size previewSize : sizes) {
            float cameraRatio = (float) previewSize.width / previewSize.height;

            //如果找不到比例相同的，找一个最近的,防止预览变形
            float offset = Math.abs(cameraRatio - minRatio);
            if (offset < minRatio) {
                minRatio = offset;
                bestSize = previewSize;
            }
            //比例相同
            if (uiRatio == cameraRatio) {
                bestSize = previewSize;
                break;
            }

        }
        return bestSize;
    }

    /**
     * 调整预览方向
     * 由于手机的图片数据都来自摄像头硬件传感器，这个传感器默认的方向横向的，所以要根据前后摄像头调整方向
     */
    private void adjustCameraOrientation() {
        //判断当前的横竖屏
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        int degress = 0;
        //获取手机的方向
        switch (rotation) {
            case Surface.ROTATION_0:
                degress = 0;
                break;
            case Surface.ROTATION_90:
                degress = 90;
                break;
            case Surface.ROTATION_180:
                degress = 180;
                break;
            case Surface.ROTATION_270:
                degress = 270;
                break;
        }
        int result = 0;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            //后置摄像头
            result = (cameraInfo.orientation - degress + 360) % 360;
        } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //前置摄像头，多一步镜像
            result = (cameraInfo.orientation + degress) % 360;
            result = (360 - result) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.stopFaceDetection();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    public void takePicture() {
        if (mCamera == null) {
            Toast.makeText(this, "请打开相机", Toast.LENGTH_SHORT).show();
            return;
        }
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                new SavePicAsyncTask(FaceCameraActivity.this, cameraInfo.facing, data).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    /**
     * 保存图片
     */
    class SavePicAsyncTask extends AsyncTask<Void, Void, File> {
        Context context;
        int facing;
        byte[] data;

        public SavePicAsyncTask(Context context, int facing, byte[] data) {
            this.context = context;
            this.facing = facing;
            this.data = data;
        }


        @Override
        protected File doInBackground(Void... voids) {
            //保存的文件
            File picFile = null;
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (bitmap == null) {
                    return null;
                }
                //保存之前先调整方向
                if (facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                } else {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

                // SD卡根目录
                File dir = context.getExternalFilesDir("print");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                picFile = new File(dir, System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(picFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                bitmap.recycle();
                return picFile;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return picFile;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file != null) {
                Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
