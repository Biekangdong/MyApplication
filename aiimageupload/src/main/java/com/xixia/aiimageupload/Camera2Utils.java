package com.xixia.aiimageupload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Camera2Utils {

    private static final String TAG = "Camera2Utils";
    private static Camera2Utils mCameraUtils;
    private CameraManager cManager;
    private Size cPixelSize;//相机成像尺寸
    private int cOrientation;
    private Size captureSize;
    private int[] faceDetectModes;
    private TextureView cView;//用于相机预览
    private Surface previewSurface;//预览Surface
    private ImageReader cImageReader;
    private Surface captureSurface;//拍照Surface
    HandlerThread cHandlerThread;//相机处理线程
    Handler cHandler;//相机处理
    CameraDevice cDevice;
    CameraCaptureSession cSession;
    CameraDevice.StateCallback cDeviceOpenCallback = null;//相机开启回调
    CaptureRequest.Builder previewRequestBuilder;//预览请求构建
    CaptureRequest previewRequest;//预览请求
    CameraCaptureSession.CaptureCallback previewCallback;//预览回调
    CaptureRequest.Builder captureRequestBuilder;
    CaptureRequest captureRequest;
    CameraCaptureSession.CaptureCallback captureCallback;

    private Context mContext;
    WindowManager mWindowManager;
    //为了使照片竖直显示
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public static Camera2Utils getInstance() {
        if (mCameraUtils == null) {
            synchronized (Camera2Utils.class) {
                if (mCameraUtils == null) {
                    mCameraUtils = new Camera2Utils();
                }
            }
        }
        return mCameraUtils;
    }

    public void init(WindowManager windowManager, Context context, TextureView textureView) {
        this.mWindowManager = windowManager;
        this.mContext = context;
        this.cView = textureView;
    }


    @SuppressLint("MissingPermission")
    public void startPreview() {
        boolean isFront = true;
        //前置摄像头
        String cId = "";
        if (isFront) {
            cId = CameraCharacteristics.LENS_FACING_BACK + "";
        } else {
            cId = CameraCharacteristics.LENS_FACING_FRONT + "";
        }

        cManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        //根据摄像头ID，开启摄像头
        try {

            //获取开启相机的相关参数
            CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);//获取预览尺寸
            Size[] captureSizes = map.getOutputSizes(ImageFormat.JPEG);//获取拍照尺寸
            cOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);//获取相机角度
            Rect cRect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);//获取成像区域
            cPixelSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);//获取成像尺寸，同上
            Log.i(TAG, "获取相机角度 : " + cOrientation);
            //可用于判断是否支持人脸检测，以及支持到哪种程度
            faceDetectModes = characteristics.get(CameraCharacteristics
                    .STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);//支持的人脸检测模式
            int maxFaceCount = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
            int mFaceDetectMode = CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF;
            for (int i = 0; i < faceDetectModes.length; i++) {
                int face = faceDetectModes[i];
                if (face == CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL || face == CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE) {
                    //Log.i(TAG, "相机硬件不支持人脸检测---" + face);
                    mFaceDetectMode = CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL;
                    break;
                }
            }
            if (mFaceDetectMode == CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF) {
                //Log.i(TAG, "相机硬件不支持人脸检测");
                return;
            }
            //支持的最大检测人脸数量
            //此处写死640*480，实际从预览尺寸列表选择
//            previewSizes[2];
            Size sSize = new Size(640, 480);
            //设置预览尺寸（避免控件尺寸与预览画面尺寸不一致时画面变形）
            transformImage(previewSizes, cView.getWidth(), cView.getHeight());
            cView.getSurfaceTexture().setDefaultBufferSize(sSize.getWidth(), sSize.getHeight());
            cManager.openCamera(cId, getCDeviceOpenCallback(), getCHandler());


        } catch (CameraAccessException e) {
        }
    }

    private void transformImage(Size[] previewSizes, int width, int height) {
        Size mPreviewSize = getOptimalSize(previewSizes, width, height);
        if (mPreviewSize == null || cView == null) {
            return;
        }
        Matrix matrix = new Matrix();
        int rotation = mWindowManager.getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centery = textureRectF.centerY();
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
        } else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(), centery - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / mPreviewSize.getWidth(), (float) height / mPreviewSize.getHeight());
            matrix.postScale(scale, scale, centerX, centery);
            matrix.postRotate(90 * (rotation - 2), centerX, centery);
            cView.setTransform(matrix);
        }
    }


    /**
     * 解决预览变形问题
     *
     * @param sizeMap
     * @param width
     * @param height
     * @return
     */
    //选择sizeMap中大于并且最接近width和height的size
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return sizeMap[0];
    }

    private Size getOptimalPreviewSize(Size[] sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        Size size = null;
        for (int i = 0; i < sizes.length; i++) {
            size = sizes[i];
            double ratio = (double) size.getWidth() / size.getHeight();
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (int i = 0; i < sizes.length; i++) {
                size = sizes[i];
                if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.getHeight() - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        int rotation = 1;
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
//            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        cView.setTransform(matrix);
    }

    /**
     * 初始化并获取相机开启回调对象。当准备就绪后，发起预览请求
     */
    @SuppressLint("NewApi")
    private CameraDevice.StateCallback getCDeviceOpenCallback() {
        if (cDeviceOpenCallback == null) {
            cDeviceOpenCallback = new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {//打开摄像头
                    cDevice = camera;
                    try {

                        //创建Session，需先完成画面呈现目标（此处为预览和拍照Surface）的初始化
                        camera.createCaptureSession(Arrays.asList(getPreviewSurface(), getCaptureSurface()), new
                                CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(CameraCaptureSession session) {
                                        cSession = session;
                                        //构建预览请求，并发起请求
                                        Log.i(TAG, "[发出预览请求]");

                                        try {
                                            session.setRepeatingRequest(getPreviewRequest(), getPreviewCallback(),
                                                    getCHandler());
                                        } catch (CameraAccessException e) {
                                            Log.i(TAG, "--" + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(CameraCaptureSession session) {
                                        session.close();
                                    }
                                }, getCHandler());
                    } catch (CameraAccessException e) {
                        Log.i(TAG, "--" + e.getMessage());
                    }
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    //关闭摄像头
                    camera.close();
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    //发生错误
                    camera.close();
                }
            };
        }
        return cDeviceOpenCallback;
    }

    /**
     * 初始化并获取相机线程处理
     *
     * @return
     */
    private Handler getCHandler() {
        if (cHandler == null) {
            //单独开一个线程给相机使用
            cHandlerThread = new HandlerThread("cHandlerThread");
            cHandlerThread.start();
            cHandler = new Handler(cHandlerThread.getLooper());
        }
        return cHandler;
    }


    /**
     * 获取支持的最高人脸检测级别
     *
     * @return
     */
    private int getFaceDetectMode() {
        if (faceDetectModes == null) {
            Log.i(TAG, "getFaceDetectMode: ----");
            return CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL;
        } else {
            Log.i(TAG, "getFaceDetectMode: --2--" + faceDetectModes[faceDetectModes.length - 1]);
            return faceDetectModes[faceDetectModes.length - 1];
        }

    }

    /**
     * 初始化并获取预览回调对象
     *
     * @return
     */
    @SuppressLint("NewApi")
    private CameraCaptureSession.CaptureCallback getPreviewCallback() {
        if (previewCallback == null) {
            previewCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest
                        request, TotalCaptureResult result) {
                    onCameraImagePreviewed(result);
                }
            };
        }
        return previewCallback;
    }

    /**
     * 生成并获取预览请求
     *
     * @return
     */
    @SuppressLint("NewApi")
    private CaptureRequest getPreviewRequest() {
        previewRequest = getPreviewRequestBuilder().build();
        return previewRequest;
    }

    /**
     * 初始化并获取预览请求构建对象，进行通用配置，并每次获取时进行人脸检测级别配置
     *
     * @return
     */
    @SuppressLint("NewApi")
    private CaptureRequest.Builder getPreviewRequestBuilder() {
        if (previewRequestBuilder == null) {
            try {
                previewRequestBuilder = cSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(getPreviewSurface());
                previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);//自动曝光、白平衡、对焦
            } catch (CameraAccessException e) {
                Log.i(TAG, "--" + e.getMessage());
            }
        }
//        previewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, getFaceDetectMode());//设置人脸检测级别
        previewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE);//设置人脸检测级别
        previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 0);
        return previewRequestBuilder;
    }


    /**
     * 获取预览Surface
     *
     * @return
     */
    private Surface getPreviewSurface() {
        if (previewSurface == null) {
            previewSurface = new Surface(cView.getSurfaceTexture());
        }

        return previewSurface;
    }

    /**
     * 处理相机画面处理完成事件，获取检测到的人脸坐标，换算并绘制方框
     *
     * @param result
     */
    @SuppressLint({"NewApi", "LocalSuppress"})
    private void onCameraImagePreviewed(CaptureResult result) {
        Face faces[] = result.get(CaptureResult.STATISTICS_FACES);
        if (faces.length > 0) {
            Log.i(TAG, "检测到有人脸，-----------------------------------");
            Log.i(TAG, "检测到有人脸，进行拍照操作：faceLength=" + faces.length);
            //检测到有人脸，控制相机进行拍照操作
            //executeCapture();
        }
    }

    /**
     * 初始化拍照相关
     */
    @SuppressLint("NewApi")
    private Surface getCaptureSurface() {
        if (cImageReader == null) {
            cImageReader = ImageReader.newInstance(getCaptureSize().getWidth(), getCaptureSize().getHeight(),
                    ImageFormat.JPEG, 2);
            cImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    //拍照最终回调
                    onCaptureFinished(reader);
                }
            }, getCHandler());
            captureSurface = cImageReader.getSurface();
        }
        return captureSurface;
    }

    /**
     * 获取拍照尺寸
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Size getCaptureSize() {
        if (captureSize != null) {
            return captureSize;
        } else {
            return new Size(cView.getWidth(), cView.getHeight());
        }
    }

    /**
     * 执行拍照
     */
    @SuppressLint("NewApi")
    private void executeCapture() {
        try {
            Log.i(TAG, "发出请求");
            cSession.capture(getCaptureRequest(), getCaptureCallback(), getCHandler());
        } catch (CameraAccessException e) {
            Log.i(TAG, "--" + e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    private CaptureRequest getCaptureRequest() {
        captureRequest = getCaptureRequestBuilder().build();
        return captureRequest;
    }

    @SuppressLint("NewApi")
    private CaptureRequest.Builder getCaptureRequestBuilder() {
        if (captureRequestBuilder == null) {
            try {
                captureRequestBuilder = cSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                //设置拍照回调接口
                captureRequestBuilder.addTarget(getCaptureSurface());
                //TODO 1 照片旋转
//                int rotation =getWindowManager().getDefaultDisplay().getRotation();
                int rotation = 0;
                int rotationTo = getOrientation(rotation);

                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotationTo);
            } catch (CameraAccessException e) {
                Log.i(TAG, "--" + e.getMessage());
            }
        }
        return captureRequestBuilder;
    }

    @SuppressLint("NewApi")
    private CameraCaptureSession.CaptureCallback getCaptureCallback() {
        if (captureCallback == null) {
            captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest
                        request, TotalCaptureResult result) {
                }
            };
        }
        return captureCallback;
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        return (ORIENTATIONS.get(rotation) + cOrientation + 270) % 360;
    }

    /**
     * 处理相机拍照完成的数据
     *
     * @param reader
     */

    Bitmap takeBitmap = null;
    Bitmap takeBitmap2 = null;

    @SuppressLint("NewApi")
    private void onCaptureFinished(ImageReader reader) {
        if (reader != null) {
            Image image = reader.acquireLatestImage();
            if (image != null && image.getPlanes() != null && image.getPlanes().length > 0) {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                image.close();
                buffer.clear();
                takeBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                /**
                 * 为了解决预览和拍照左右颠倒问题
                 */
                Matrix m = new Matrix();
                m.postScale(-1, 1); // 镜像水平翻转
                takeBitmap2 = Bitmap.createBitmap(takeBitmap, 0, 0, takeBitmap.getWidth(), takeBitmap.getHeight(), m, true);
                if (ioShowBitmapListener != null) {
                    ioShowBitmapListener.showBitmap(takeBitmap2);
                }
//        Runtime.getRuntime()
//                .gc();
            }
        }
    }


    @SuppressLint("NewApi")
    public void closeCamera() {
        if (cSession != null) {
            try {
                cSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            cSession.close();
            cSession = null;
        }

        if (cDevice != null) {
            cDevice.close();
            cDevice = null;
        }
        if (cImageReader != null) {
            cImageReader.close();
            cImageReader = null;
            captureRequestBuilder = null;
        }
        if (cHandlerThread != null) {
            cHandlerThread.quitSafely();
            try {
                cHandlerThread.join();
                cHandlerThread = null;
                cHandler = null;
            } catch (InterruptedException e) {
                Log.i(TAG, "--" + e.getMessage());
            }
        }

        if (captureRequestBuilder != null) {
            captureRequestBuilder.removeTarget(captureSurface);
            captureRequestBuilder = null;
        }
        if (captureSurface != null) {
            captureSurface.release();
            captureSurface = null;
        }
        if (previewRequestBuilder != null) {
            previewRequestBuilder.removeTarget(previewSurface);
            previewRequestBuilder = null;
        }
        if (previewSurface != null) {
            previewSurface.release();
            previewSurface = null;
        }

        if (takeBitmap != null) {
            takeBitmap.recycle();
            takeBitmap = null;
        }

    }


    private IOShowBitmapListener ioShowBitmapListener;

    public void setIoShowBitmapListener(IOShowBitmapListener ioShowBitmapListener) {
        this.ioShowBitmapListener = ioShowBitmapListener;
    }

    public interface IOShowBitmapListener {
        void showBitmap(Bitmap bitmap);
    }

}

