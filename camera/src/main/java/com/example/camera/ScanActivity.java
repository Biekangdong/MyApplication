/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.camera.decode.HuaweiHmsUtils;
import com.example.camera.decode.SunmiUtils;
import com.example.camera.decode.ZxingUtils;
import com.lensun.lensuncustomizpro.R;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;


public class ScanActivity extends Activity {

    public static final int REQUEST_CODE_PHOTO = 0X1113;
    private static final String TAG = "ScanActivity";

    private Camera camera = null;
    private Camera.Parameters parameters = null;
    private boolean isPreview = false;
    private FrameCallback frameCallback = new FrameCallback();
    private int width = 1920;
    private int height = 1080;
    private double defaultZoom = 1.0;

    private SurfaceHolder surfaceHolder;
    private SurfaceCallBack surfaceCallBack;
    private CommonHandler handler;
    private boolean isShow;

    private FrameLayout rim;
    private SurfaceView surfaceView;
    private ImageView backImg;
    private TextView tvDecode;
    private ImageView imgBtn;


    private int shortSize;
    private int longSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common);

        rim = (FrameLayout) findViewById(R.id.rim);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        backImg = (ImageView) findViewById(R.id.back_img);
        tvDecode = (TextView) findViewById(R.id.tv_decode);
        imgBtn = (ImageView) findViewById(R.id.img_btn);


        checkCameraPermission();
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


    /**
     * 创建预览
     */
    private void createSurfaceView() {
        surfaceCallBack = new SurfaceCallBack();

        adjustSurface(surfaceView);
        surfaceHolder = surfaceView.getHolder();
        isShow = false;
        setBackOperation();
        setPictureScanOperation();
        setDecodeSelectOperation();
    }

    private void adjustSurface(SurfaceView cameraPreview) {
        FrameLayout.LayoutParams paramSurface = (FrameLayout.LayoutParams) cameraPreview.getLayoutParams();
        if (getSystemService(Context.WINDOW_SERVICE) != null) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display defaultDisplay = windowManager.getDefaultDisplay();
            Point outPoint = new Point();
            defaultDisplay.getRealSize(outPoint);
            int sceenWidth = outPoint.x;
            int sceenHeight = outPoint.y;

            shortSize = Math.min(sceenWidth, sceenHeight);
            longSize = shortSize;
            //横屏
            paramSurface.width = shortSize;
            paramSurface.height = shortSize;


//            float rate;
//            if (sceenWidth / (float) 1080 > sceenHeight / (float) 1920) {
//                rate = sceenWidth / (float) 1080;
//                int targetHeight = (int) (1920 * rate);
//                paramSurface.width = FrameLayout.LayoutParams.MATCH_PARENT;
//                paramSurface.height = targetHeight;
//                int topMargin = (int) (-(targetHeight - sceenHeight) / 2);
//                if (topMargin < 0) {
//                    paramSurface.topMargin = topMargin;
//                }
//            } else {
//                rate = sceenHeight / (float) 1920;
//                int targetWidth = (int) (1080 * rate);
//                paramSurface.width = targetWidth;
//                paramSurface.height = FrameLayout.LayoutParams.MATCH_PARENT;
//                int leftMargin = (int) (-(targetWidth - sceenWidth) / 2);
//                if (leftMargin < 0) {
//                    paramSurface.leftMargin = leftMargin;
//                }
//            }
        }
    }

    private void setBackOperation() {
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setPictureScanOperation() {
        imgBtn = findViewById(R.id.img_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                ScanActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
            }
        });
    }

    private int decodeType = 1;
    private void setDecodeSelectOperation() {
        tvDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (decodeType == 1) {
                    decodeType = 2;
                    tvDecode.setText("Sunmin");
                } else if (decodeType == 2) {
                    decodeType = 3;
                    tvDecode.setText("HuaweiHms");
                } else if (decodeType == 3) {
                    decodeType = 1;
                    tvDecode.setText("Zxing");
                }

            }
        });
        tvDecode.setText("Zxing");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isShow) {
            initCamera();
        } else {
            surfaceHolder.addCallback(surfaceCallBack);
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quit();
            handler = null;
        }
        close();
        if (!isShow) {
            surfaceHolder.removeCallback(surfaceCallBack);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initCamera() {
        open(surfaceHolder);
        if (handler == null) {
            handler = new CommonHandler();
        } else {
            startPreview();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK || data == null || requestCode != REQUEST_CODE_PHOTO) {
//            return;
//        }
//        try {
//            decodeMultiSyn(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
//        } catch (Exception e) {
//            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
//        }
    }


    class SurfaceCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isShow) {
                isShow = true;
                initCamera();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            isShow = false;
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Open up the camera.
     */
    public synchronized void open(SurfaceHolder holder) {
        try {
            camera = Camera.open(0);
            parameters = camera.getParameters();
            //获取合适的预览尺寸，保证不变形
            Camera.Size bestSize = getBestSize(parameters);
            //设置预览大小
            parameters.setPreviewSize(bestSize.width, bestSize.height);
            parameters.setPictureSize(width, height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setPictureFormat(ImageFormat.NV21);
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            //camera.setDisplayOrientation(270);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取预览最佳尺寸
     */
    private Camera.Size getBestSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
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


    public synchronized void close() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public synchronized void startPreview() {
        if (camera != null && !isPreview) {
            camera.startPreview();
            isPreview = true;
        }
    }

    public synchronized void stopPreview() {
        if (camera != null && isPreview) {
            camera.stopPreview();
            frameCallback.setProperties(null);
            isPreview = false;
        }
    }

    public synchronized void callbackFrame(Handler handler, double zoomValue) {
        if (camera != null && isPreview) {
            frameCallback.setProperties(handler);
            if (camera.getParameters().isZoomSupported() && zoomValue != defaultZoom) {
                //Auto zoom.
                //parameters.setZoom(convertZoomInt(zoomValue));
                camera.setParameters(parameters);
            }
            camera.setOneShotPreviewCallback(frameCallback);
        }
    }

    public int convertZoomInt(double zoomValue) {
        List<Integer> allZoomRatios = parameters.getZoomRatios();
        float maxZoom = Math.round(allZoomRatios.get(allZoomRatios.size() - 1) / 100f);
        if (zoomValue >= maxZoom) {
            return allZoomRatios.size() - 1;
        }
        for (int i = 1; i < allZoomRatios.size(); i++) {
            if (allZoomRatios.get(i) >= (zoomValue * 100) && allZoomRatios.get(i - 1) <= (zoomValue * 100)) {
                return i;
            }
        }
        return -1;
    }


    class FrameCallback implements Camera.PreviewCallback {

        private Handler handler;

        public void setProperties(Handler handler) {
            this.handler = handler;

        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (handler != null) {
                Message message = handler.obtainMessage(0, camera.getParameters().getPreviewSize().width,
                        camera.getParameters().getPreviewSize().height, data);
                message.sendToTarget();
                handler = null;
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////


    class CommonHandler extends Handler {

        private static final String TAG = "MainHandler";
        private static final double DEFAULT_ZOOM = 1.0;
        private HandlerThread decodeThread;
        private Handler decodeHandle;
        private BeepManager beepManager;
        private long preTime;
        public CommonHandler() {
            beepManager = new BeepManager(ScanActivity.this);

            decodeThread = new HandlerThread("DecodeThread");
            decodeThread.start();
            decodeHandle = new Handler(decodeThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg == null) {
                        return;
                    }
                    long startTime=System.currentTimeMillis();
                    String result = decodeSyn(msg.arg1, msg.arg2, (byte[]) msg.obj);
                    long endTime=System.currentTimeMillis();
                    if (result == null) {
                        restart(DEFAULT_ZOOM);
                    } else {
                        Message message = new Message();
                        message.what = msg.what;
                        message.obj = result;
                        message.arg1= (int) (endTime-startTime);
                        CommonHandler.this.sendMessage(message);
                    }
                }
            };
            startPreview();
            restart(DEFAULT_ZOOM);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                String result = (String) message.obj;
                Log.e(TAG, result);
                long currentTime = System.currentTimeMillis();
                if (currentTime - preTime > 200) {
                    preTime = currentTime;
                    beepManager.playBeepSoundAndVibrate();
                    restart(DEFAULT_ZOOM);
                    stopPreview();

                    Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                    intent.putExtra("result", result);
                    intent.putExtra("time", message.arg1);
                    startActivity(intent);

                }

            }
        }

        public void quit() {
            try {
                stopPreview();
                decodeHandle.getLooper().quit();
                decodeThread.join(500);
            } catch (InterruptedException e) {
                Log.w(TAG, e);
            }
        }

        public void restart(double zoomValue) {
            callbackFrame(decodeHandle, zoomValue);
        }

        /**
         * Call the MultiProcessor API in synchronous mode.
         */
        private String decodeSyn(int width, int height, byte[] data) {
            String result="";
            switch (decodeType){
                case 1://Zxing
                    result = ZxingUtils.decode(width, height, data);
                    break;
                case 2://商米
                    result = SunmiUtils.decode(width, height, data);
                    break;
                case 3://华为
                    result = HuaweiHmsUtils.decode(width, height, data, ScanActivity.this);
                    break;
            }
            return result;
        }
    }
}
