package com.xixia.aiimageupload.opcv;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.xixia.aiimageupload.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceCamera2Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "TakePhotoFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    /*fragment空间声明*/
    private TextureView textureView;
    private Button takePhotoBtn;
    private Button mImageView;
    /*private MyImageView mImageView;*/
    private Button changeCamBtn;
    /*除此之外，还需要一些参数*/
    private String mCameraId; //摄像头ID
    private Size previewSize; //预览分辨率
    private ImageReader mImageReader; //图片阅读器
    private static CameraDevice mCameraDevice;   //摄像头设备
    private static CameraCaptureSession mCaptureSession;   //获取会话
    private CaptureRequest mPreviewRequest;      //获取预览请求
    private CaptureRequest.Builder mPreviewRequestBuilder;   //获取到预览请求的Builder通过它创建预览请求
    private Surface mPreviewSurface;  //预览显示图
    //新建一个权限链
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private List<String> permissionList = new ArrayList();
    //添加一个图片集合
    List<String> imageList = new ArrayList<>();
    private Boolean isCreated = false;
    private Boolean isLeave = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_face_camera2);
        initView();
        textureView.setSurfaceTextureListener(textureListener);
        //动态授权
        getPermission();
        isCreated = true;
    }


    //看看viewpage对fragment的生命周期的影响
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: success");
        closeCamera();
        System.out.println(textureView.isAvailable());
        if (textureView.isAvailable()) {
            //如果可用，就是除了进行切换pageview之外的所有操作
            isLeave = true;
        } else {
            //不可用说明就是直接切换了pageview
            closeCamera();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: success");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: success");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: success");
        System.out.println("++++++++++");
        System.out.println(textureView.isAvailable());
        if (textureView.isAvailable()) {
            if (isLeave) {
                //息屏等操作会自动关闭camera，所以就得手动再打开一次
                openCamera();
                isLeave = false;
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: success");
        /*if (textureView.isAvailable()){
            openCamera();
        }*/
    }


    private void closeCamera() {
        Log.d(TAG, "closeCamera: success");
        //首先要关闭session
        if (mCaptureSession != null) {
            mCaptureSession.close();
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
        }
    }

    //绑定控件
    private void initView() {
        textureView = findViewById(R.id.textureView);
        takePhotoBtn = findViewById(R.id.takePicture);
        mImageView = findViewById(R.id.image_show);
        changeCamBtn = findViewById(R.id.change);
        changeCamBtn.setOnClickListener(this);
    }

    private void getPermission() {
        Log.d(TAG, "getPermission: success");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (!permissionList.isEmpty()) {
                //进行授权
                ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
            } else {
                //表示全部已经授权
                //这时候回调一个预览view的回调函数
                textureView.setSurfaceTextureListener(textureListener);
            }
        }
    }
    //只能写在Activity中，下次把授权写到activity中，减少麻烦
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: success");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults.length!=0){
                //表示有权限没有授权
                getPermission();
            }
            else {
                //表示都授权
                openCamera();
            }
        }
    }

    /*SurfaceView状态回调*/
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable: success");
            //首先就需要设置相机，然后再打开相机
            setLastImagePath();
            setupCamera(width, height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void setupCamera(int width, int height) {
        Log.d(TAG, "setupCamera: success");
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String cameraId : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //相机支持的所有分辨率，下一步就是获取最合适的分辨率
                Size[] outputSizes = map.getOutputSizes(SurfaceTexture.class);
                Size size = getOptimalSize(outputSizes, width, height);
                previewSize = size;
                mCameraId = cameraId;
                break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //打开摄像头
    private void openCamera() {
        Log.d(TAG, "openCamera: success");
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("该应用需要相机授权，点击授权按钮跳转到设置进行设置");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                    }
                }).create().show();
                return;
            }
            cameraManager.openCamera(mCameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //摄像头状态回调
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "onOpened: success");
            mCameraDevice = camera;
            //开启预览
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Toast.makeText(mContext, "摄像头设备连接失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Toast.makeText(mContext, "摄像头设备连接出错", Toast.LENGTH_SHORT).show();
        }
    };


    //预览功能
    private void startPreview() {
        Log.d(TAG, "startPreview: success");
        //设置图片阅读器
        setImageReader();
        //注意这里：sufacetexture跟surfaceview是两个东西，需要注意！
        //sufacetexture是textureview的重要属性
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        //设置textureview的缓存区大小
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        //设置surface进行预览图像数据
        mPreviewSurface = new Surface(surfaceTexture);
        //创建CaptureRequest
        setCaptureRequest();
        //创建capturesession
        /*Surface表示有多个输出流，我们有几个显示载体，就需要几个输出流。
        对于拍照而言，有两个输出流：一个用于预览、一个用于拍照。
        对于录制视频而言，有两个输出流：一个用于预览、一个用于录制视频。*/
        // previewSurface 用于预览， mImageReader.getSurface() 用于拍照
        try {
            mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    //当回调创建成功就会调用这个回调
                    mCaptureSession = session;
                    setRepeatCapture();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    //设置图片阅读器
    private void setImageReader() {
        Log.d(TAG, "setImageReader: success");
        //创建ImageReader实例，接下来应该是设置一些属性参数
        mImageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 1);
        //果然跟我想的一样，接下来是设置监听当图片流可用的时候的监听器,即为拍照之后产生照片流
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
                //进行保存图片，开一个线程进行保存
                ImageSaver imageSaver = new ImageSaver(mContext, image);
                new Thread(imageSaver).start();
                Toast.makeText(mContext, "保存图片成功", Toast.LENGTH_SHORT).show();
            }
        }, null);
    }

    //选择sizeMap中大于并且接近width和height的size
    private Size getOptimalSize(Size[] outputSizes, int width, int height) {
        Size tempSize = new Size(width, height);
        List<Size> sizes = new ArrayList<>();
        for (Size outputSize : outputSizes) {
            if (width > height) {
                //横屏的时候
                if (outputSize.getHeight() > height && outputSize.getWidth() > width) {
                    sizes.add(outputSize);
                }
            } else {
                //竖屏的时候
                if (outputSize.getWidth() > height && outputSize.getHeight() > width) {
                    sizes.add(outputSize);
                }
            }
        }
        if (sizes.size() > 0) {
            //如果有多个符合条件找到一个差距最小的，最接近预览分辨率的
            tempSize = sizes.get(0);
            int minnum = 999999;
            for (Size size : sizes) {
                int num = size.getHeight() * size.getHeight() - width * height;
                if (num < minnum) {
                    minnum = num;
                    tempSize = size;
                }
            }
        }
        return tempSize;
        /*if (sizes.size() > 0) {
            return Collections.min(sizes, new Comparator<Size>() {
                @Override
                public int compare(Size size, Size t1) {
                    return Long.signum(size.getWidth() * size.getHeight() - t1.getWidth() * t1.getHeight());
                }
            });
        }
        return outputSizes[0];*/

    }

    /*Fragment控件点击事件*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change:
                changeLens();
                break;
        }
    }

    private void changeLens() {
        Log.d(TAG, "changeLens: success");
        if (mCameraId.equals(String.valueOf(CameraCharacteristics.LENS_FACING_BACK))) {
            mCameraId = String.valueOf(CameraCharacteristics.LENS_FACING_FRONT);
        } else {
            if (mCameraId.equals(String.valueOf(CameraCharacteristics.LENS_FACING_FRONT))) {
                mCameraId = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);
            }
        }
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] outputSizes = map.getOutputSizes(SurfaceTexture.class);
            Size optimalSize = getOptimalSize(outputSizes, textureView.getWidth(), textureView.getHeight());
            previewSize = optimalSize;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        /*这里不能直接使用关闭camera这个方法*/
        /*closeCamera();*/
        closeCamera();
        openCamera();
    }

    //创建一个图片保存类
    public class ImageSaver implements Runnable {
        private Context context;
        private Image image;

        public ImageSaver(Context context, Image image) {
            Log.d(TAG, "ImageSaver: success");
            this.context = context;
            this.image = image;
        }

        @Override
        public void run() {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            System.out.println(planes);
            String filname = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + System.currentTimeMillis() + ".jpg";
            File file = new File(filname);
            FileOutputStream fileOutputStream = null;
            try {
                //保存图片
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes, 0, bytes.length);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //最后还要广播通知相册更新数据库
                notiBroadcast();
                //保存操作结束后，需要用handle进行主线程数据的更新
                Message message = new Message();
                message.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("path", filname);
                message.setData(bundle);
                handler.sendMessage(message);
                //image也算是个流也需要进行关闭，否则可能下一次拍照的时候会报错
                image.close();
            }
        }
    }

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Bundle pathdata = msg.getData();
                String path = (String) pathdata.get("path");
                imageList.add(path);
                //设置拍照界面显示的一个图片（左下角的图片预览）
                setLastImagePath();
            }
        }
    };

    private void notiBroadcast() {
        String path = Environment.getExternalStorageDirectory() + "/DCIM/";
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        mContext.sendBroadcast(intent);
    }

    private void setLastImagePath() {
        Log.d(TAG, "setLastImagePath: success");
        //先判断一下手机有没有权限
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //拿到最后一张拍照照片，遍历所有相册照片
        String DCIMPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
        File file = new File(DCIMPath);
        //对该文件夹进行遍历
        String[] jpgs = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains("jpg")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        String finalImagePath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + jpgs[jpgs.length - 1];
        Bitmap bitmap = BitmapFactory.decodeFile(finalImagePath);
        /*Canvas canvas = new Canvas();
        canvas.drawBitmap(bitmap,0,0,new Paint());
        canvas.drawCircle(bitmap.getWidth()/2,bitmap.getHeight()/2,bitmap.getWidth()/2,new Paint());
        mImageView.draw(canvas);*/
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        /*mImageView.setImageBitmap(bitmap);*/
        // mImageView.setImageDrawable(roundedBitmapDrawable);

    }

    //无论是预览还是拍照都需要设置capturerequest
    private void setCaptureRequest() {
        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mPreviewSurface);
            //手动对焦，还不会
            /*
                TODO
             */
            // 自动对焦
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 闪光灯
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 人脸检测模式
            mPreviewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setRepeatCapture() {
        Log.d(TAG, "setRepeatCapture: success");
        mPreviewRequestBuilder.setTag(TAG);
        //首先要知道整个调用顺序 devices创建出capturebuilder，当builder设置好各种参数之后，就可以build出capturerequire
        mPreviewRequest = mPreviewRequestBuilder.build();
        //session中需要用到capturerequire
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequest, new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}