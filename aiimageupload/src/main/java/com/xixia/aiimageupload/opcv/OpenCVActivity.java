package com.xixia.aiimageupload.opcv;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.sun.org.apache.xml.internal.utils.StringVector;
import com.xixia.aiimageupload.PictureSelectorUtils;
import com.xixia.aiimageupload.R;
import com.xixia.aiimageupload.icc.ICCActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.osgi.OpenCVNativeLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OpenCVActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/9/9 17:45
 * @Version 1.0
 * @UpdateDate 2023/9/9 17:45
 * @UpdateRemark 更新说明
 */
public class OpenCVActivity extends Activity {
    private static final String TAG = "OpenCVActivity";
    private ImageView ivImage1;
    private ImageView ivImage2;


    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv);

        ivImage1 = (ImageView) findViewById(R.id.iv_image1);
        ivImage2 = (ImageView) findViewById(R.id.iv_image2);

        initOpenCV();
    }

    public void selectClick(View view) {
        permissionChecker();
    }


    public void chanleClick(View view) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Mat mat = getOriginMat(bitmap);
    }

    public void grayClick(View view) {
        Bitmap bitmapResult = cvtColorBitmap();
        ivImage2.setImageBitmap(bitmapResult);
    }

    public void gray2Click(View view) {
        Bitmap bitmapResult = cvtColor2Bitmap();
        ivImage2.setImageBitmap(bitmapResult);
    }

    public void circleClick(View view) {
        Bitmap bitmapResult = drawCircle();
        ivImage2.setImageBitmap(bitmapResult);
    }

    public void colorRangeClick(View view) {
        Bitmap bitmapResult = colorRange();
        ivImage2.setImageBitmap(bitmapResult);
    }


    public void cvClick(View view) {
        Bitmap resultBitmap = findContours();
        ivImage2.setImageBitmap(resultBitmap);
    }

    public void qrcodeClick(View view){
        Bitmap resultBitmap = QRcodeOpenCVUtils.mainQrcodeDecode(filePath);
        ivImage2.setImageBitmap(resultBitmap);
    }

    public void faceClick(View view){
        Bitmap resultBitmap = detectFace(filePath);
        ivImage2.setImageBitmap(resultBitmap);
    }

    public void moreClick(View view) {
        startActivity(new Intent(this, IniyializeActivity.class));
    }

    /**
     * 检查权限
     */
    private void permissionChecker() {
        //String permission= Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU? Manifest.permission.READ_MEDIA_IMAGES:Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (PermissionChecker.checkSelfPermission(this, permission)) {
            initPhotoPickerSingle();
        } else {
            PermissionChecker.requestPermissions(this, new String[]{permission}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    /**
     * 选择图片
     */
    private void initPhotoPickerSingle() {
        PictureSelectorUtils.initPhotoPickerSingle(this, new PictureSelectorUtils.OnPictureSelectorListener() {
            @Override
            public void selectResult(String mfilePath) {
                filePath = mfilePath;
                Glide.with(OpenCVActivity.this).load(filePath).into(ivImage1);
               String base64String=imageToBase64(new File(filePath));
                Log.e(TAG, "onResult: " + base64String);

            }
        });
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(File file){
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(file);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 初始化OpenCV
     */
    private void initOpenCV() {
        // OpenCV提供的加载libopencv_java4.so的封装类
        OpenCVNativeLoader openCVNativeLoader = new OpenCVNativeLoader();
        openCVNativeLoader.init();
    }

    /**
     * 获取通道和像素数量
     *
     * @param bitmap
     * @return
     */
    private Mat getOriginMat(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1, buffer);
        //通道和像素
        Log.e(TAG, "mat channels:" + mat.channels() + ", cols:" + mat.cols() + ", rows:" + mat.rows());
        return mat;
    }


    /**
     * 转换灰度
     *
     * @return
     */
    private Bitmap cvtColorBitmap() {
        Bitmap bp = BitmapFactory.decodeFile(filePath);
        Mat src = new Mat(bp.getHeight(), bp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bp, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        Utils.matToBitmap(src, bp);
        return bp;
    }

    /**
     * 手动阀值二值转换
     *
     * @return
     */
    private Bitmap cvtColor2Bitmap() {
        Bitmap bp = BitmapFactory.decodeFile(filePath);
        Mat src = new Mat(bp.getHeight(), bp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bp, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        //手动
        //Imgproc.threshold(src,src,125,255,Imgproc.THRESH_BINARY);
        //自动
        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 13, 5);
        Utils.matToBitmap(src, bp);
        return bp;
    }


    /**
     * 绘制圆形
     */
    private Bitmap drawCircle() {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Mat matrix = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, matrix);
        //Drawing a Circle
        Imgproc.circle(
                matrix,                 //Matrix obj of the image
                new Point(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f),    //Center of the circle
                300,                    //Radius
                new Scalar(0, 255, 255),  //Scalar object for color
                2                      //圆线的厚度
        );

        Utils.matToBitmap(matrix, bitmap);
        return bitmap;
    }


    //颜色检测切割
    public Bitmap colorRange() {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Mat dstmat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, dstmat);

        //颜色转换
        Mat hsvmat = new Mat();
        Imgproc.cvtColor(dstmat, hsvmat, Imgproc.COLOR_RGB2HSV);

        //检测颜色
        Core.inRange(hsvmat, new Scalar(160, 90, 90), new Scalar(179, 255, 255), hsvmat);

        //开运算和闭运算，降低噪点
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(hsvmat, hsvmat, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(hsvmat, hsvmat, Imgproc.MORPH_CLOSE, kernel);

        //转换成Bitmap
        Utils.matToBitmap(hsvmat, bitmap);
        return bitmap;
    }


    /**
     * @param img Canny边缘检测
     * @return
     */
    public void cannyEdges(Mat img) {
        List<MatOfPoint> pts = new ArrayList<>();
        Mat canny = new Mat();
        Imgproc.Canny(img, canny, 30, 100);
        Scalar color = new Scalar(255, 0, 0, 255);
        Imgproc.fillPoly(img, pts, color);

    }


    /**
     * 查找和绘制轮廓
     *
     * @param binImg
     */
    public Bitmap shapeDetection(Mat binImg) {
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        List<MatOfPoint> selectedContours = new ArrayList<>();
        Imgproc.findContours(binImg, contourList, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contourList.size(); i++) {
            MatOfPoint2f point = new MatOfPoint2f();
            point.fromList(contourList.get(i).toList());
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            double parameter = Imgproc.arcLength(point, true);
            Imgproc.approxPolyDP(point, approxCurve, parameter * 0.02, true);
            long total = approxCurve.total();
            //Detecting Rectangle Shape
            if (total == 4) {
                double area = Imgproc.contourArea(contourList.get(i));
                //rectangle with area greater than 500
                if (area > 500)
                    selectedContours.add(contourList.get(i));
            }
        }

        if (selectedContours.size() <= 0) {
            return null;
        }

        Mat org = new Mat();
        Imgproc.drawContours(org, selectedContours, -1, new Scalar(50, 205, 50), 3);

        Bitmap bitmap = Bitmap.createBitmap(org.cols(), org.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(org, bitmap);
        return bitmap;
    }


    /**
     * @param
     * @return
     */
    public Bitmap findContours() {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Mat dstmat = getOriginMat(bitmap);
//        Mat dstmat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
//        Utils.bitmapToMat(bitmap, dstmat);

        List<MatOfPoint> matOfPointList = new ArrayList<>();
        /**
         * image，输入图像，通常为8通道二值图像。在OpenCV3.2版本后，轮廓提取方式是RETR_CCOMP或者RECT_FLOODFILL时，也可以输入32位单通道整形图像（CV_32SC1）。
         * contours，输出找到的轮廓。
         * hierarchy，可选项，输出所有轮廓的树结构。
         * mode，轮廓提取方式。
         * method，轮廓近似方式。
         * offset，可选项，返回的轮廓中所有点都会根据设置的参数值发生偏移。
         */
        /**
         * RETR_EXTERNAL，只检索最外层轮廓。
         * RETR_LIST，检测所有轮廓并保存到列表中。
         * RETR_CCMOP，检测所有轮廓，并将它们组织成双层结构，顶层是所有成分的外部边界，第二层是内部空的孔的边界。
         * RETR_TREE，检测所有轮库并重新建立网状轮廓结构。
         * RECT_FLOODFILL，尚未在官网上查到解释。
         */

        /**
         * CHAIN_APPROX_NONE，将轮廓编码中的所有点转换为点。
         * CHAIN_APPROX_SIMPLE，压缩水平、垂直、倾斜部分，只保留最后一个点。
         * CHAIN_APPROX_TC89_L1或CHAIN_APPROX_TC89_L1，使用Teh-Chin链逼近算法中的一个。
         */
        Imgproc.findContours(dstmat, matOfPointList, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Log.e(TAG, "cannyEdges: " + matOfPointList.size());
        Imgproc.drawContours(dstmat, matOfPointList, -1, new Scalar(50, 205, 50), 3);

        //转换成Bitmap
        Utils.matToBitmap(dstmat, bitmap);
        return bitmap;
        //return drawResult(bitmap, pts);
    }

    /**
     * java 绘制检测的轮廓
     *
     * @param originBitmap
     * @param pts
     * @return
     */
    private Bitmap drawResult(Bitmap originBitmap, List<MatOfPoint> pts) {
        int margin = 8;
        float shift = margin / 2f;
        //创建画布
        Bitmap bitmap = Bitmap.createBitmap(originBitmap.getWidth() + margin, originBitmap.getHeight() + margin,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        for (MatOfPoint matOfPoint : pts) {
            Path path = new Path();
            List<Point> mPointList = matOfPoint.toList();
            for (Point point : mPointList) {
                path.lineTo((float) point.x + shift, (float) point.y + shift);
            }
            if (mPointList.size() > 0) {
                Point firstPoint = mPointList.get(0);
                path.lineTo((float) firstPoint.x + shift, (float) firstPoint.y + shift);
            }

            canvas.drawPath(path, paint);
        }

        return bitmap;
    }


//    public static String deCode(Mat img) {
//        //微信二维码对象，要返回二维码坐标前2个参数必传；后2个在二维码小或不清晰时必传。
//        WeChatQRCode we = new WeChatQRCode();
//        List<Mat> points = new ArrayList<Mat>();
//        //微信二维码引擎解码，返回的valList中存放的是解码后的数据，points中Mat存放的是二维码4个角的坐标
//        StringVector stringVector = we.detectAndDecode(img);
//
//        System.out.println(stringVector.get(0).getString(StandardCharsets.UTF_8));
//        return stringVector.get(0).getString(StandardCharsets.UTF_8);
//    }


    /**
     * 提取条形码区域
     * 1. 原图像大小调整，提高运算效率
     * 2. 转化为灰度图
     * 3. 高斯平滑滤波
     * 4.求得水平和垂直方向灰度图像的梯度差,使用Sobel算子
     * 5.均值滤波，消除高频噪声
     * 6.二值化
     * 7.闭运算，填充条形码间隙
     * 8. 腐蚀，去除孤立的点
     * 9. 膨胀，填充条形码间空隙，根据核的大小，有可能需要2~3次膨胀操作
     * 10.通过findContours找到条形码区域的矩形边界
     *
     * @return
     */
    public Bitmap getImageDiscriminatePoint() {
        Bitmap source = BitmapFactory.decodeFile(filePath);

        Mat imageSobelX = new Mat();
        Mat imageSobelY = new Mat();
        Mat imageSobelOut = new Mat();

        Mat image = new Mat();
        Mat imageGray = new Mat();
        Mat imageGuussian = new Mat();

        Utils.bitmapToMat(source, image);

        //1:调整图片大小
        Imgproc.resize(image, image, new Size(image.rows() / 4, image.cols() / 4));

        //2:灰度化
        Imgproc.cvtColor(image, imageGray, Imgproc.COLOR_BGR2GRAY);

        //3:高斯平滑, Imgproc.getGaussianKernel();高斯滤波
        Imgproc.GaussianBlur(imageGray, imageGuussian, new Size(3, 3), 0);

        //4：求得水平和垂直方向灰度图像的梯度差,使用Sobel算子
        Mat imageX16S = new Mat();
        Mat imageY16S = new Mat();
        Imgproc.Sobel(imageGuussian, imageX16S, CvType.CV_16S, 1, 0, 3, 1, 0, 4);
        Imgproc.Sobel(imageGuussian, imageY16S, CvType.CV_16S, 0, 1, 3, 1, 0, 4);


        Core.convertScaleAbs(imageX16S, imageSobelX, 1, 0);
        Core.convertScaleAbs(imageY16S, imageSobelY, 1, 0);
        // imageSobelOut = imageSobelX - imageSobelY;

        Core.addWeighted(imageSobelX, 0.5, imageSobelY, 0.5, 1, imageSobelOut);//计算梯度和
        //Core.divide(imageSobelX, imageSobelY, imageSobelOut);


        //5：均值滤波，消除高频噪声
        Imgproc.blur(imageSobelOut, imageSobelOut, new Size(3, 3));


        //6：二值化
//        Mat imageSobleOutThreshold = new Mat();
//        Imgproc.threshold(imageSobelOut, imageSobleOutThreshold, 100, 255, Imgproc.THRESH_BINARY);

        //7.闭运算，填充条形码间隙
//        Mat  element = Imgproc.getStructuringElement(0, new Size(7, 7));
//        Imgproc.morphologyEx(imageSobleOutThreshold, imageSobleOutThreshold, Imgproc.MORPH_CLOSE, element);


//        //8. 腐蚀，去除孤立的点
//       // Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
//        Imgproc.erode(imageSobleOutThreshold, imageSobleOutThreshold, element);
//
//        //9. 膨胀，填充条形码间空隙，根据核的大小，有可能需要2~3次膨胀操作
//        Imgproc.dilate(imageSobleOutThreshold,imageSobleOutThreshold,element);

        image = imageSobelOut;

        //10.通过findContours找到条形码区域的矩形边界
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Mat hierarcy = new Mat();
//        Imgproc.findContours(imageSobleOutThreshold, contours, hierarcy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
//
//        Log.d("-----------------", "求灰度图"+contours.size());
//        for(int i=0;i<contours.size();i++) {
        //   Rect rect = Imgproc.boundingRect(contours.get(i));
//            Log.d("-----------------", "这里知心了"+rect.x+"------"+rect.y+"-----"+rect.width+"-------"+rect.height);
//            Imgproc.rectangle(image, new Point(rect.x + rect.width, rect.y + rect.height),new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(177));
//        }


        Bitmap idcardBit = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.RGB_565);//ARGB_8888,RGB_565
        Utils.matToBitmap(image, idcardBit);

        //腐蚀
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
//        Imgproc.erode(idcardMat, idcardMat, kernel);

//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(50, 50));
//        Imgproc.erode(idcardMat, idcardMat, kernel);


//        File file = new File(Environment.getExternalStorageDirectory()+"/AiLingGong/", "test.jpg");
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            idcardBit.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//        } catch (FileNotFoundException e) {
//            Log.d("-----------------", "111111");
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.d("----------------", "2222222");
//            e.printStackTrace();
//        }


//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Mat hierarcy = new Mat();
//        Imgproc.findContours(idcardMat, contours, hierarcy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        int idCardNumberY = 0;
//
//        for(int i=0;i<contours.size();i++){
//            Rect rect = Imgproc.boundingRect(contours.get(i));
//            Imgproc.rectangle(idcardMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0), 3);
//            if(rect.width > 500 && rect.width/rect.height >= 6){
//                //这里可能取到多个轮廓噢，“地址的轮廓也可能会进来”，需要简单筛选一下下面的轮廓，（之前bug原因，腐蚀不够高，大量轮廓进来了）
//                if (idCardNumberY < rect.y) {
//                    idCardNumberY = rect.y;
//                    idcardBit = cropDownPart(source, rect.x, rect.y, rect.width, rect.height);
//                }
//
//                File file = new File(Environment.getExternalStorageDirectory()+"/AiLingGong/", "w"+rect.width+"h"+rect.height+".jpg");
//                try {
//                    FileOutputStream out = new FileOutputStream(file);
//                    idcardBit.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    out.flush();
//                    out.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return idcardBit;
    }


    /**
     * 检测人脸
     */
    private CascadeClassifier mJavaDetector; // OpenCV的人脸检测器
    private Bitmap detectFace(String filePath) {
        Bitmap orig = BitmapFactory.decodeFile(filePath);

        Mat rgba = new Mat();
        Utils.bitmapToMat(orig, rgba); // 把位图对象转为Mat结构
        //Mat rgba = Imgcodecs.imread(mFilePath); // 从文件路径读取Mat结构
        //Imgcodecs.imwrite(tempFile.getAbsolutePath(), rgba); // 把Mate结构保存为文件
        Mat gray = new Mat();
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGB2GRAY); // 全彩矩阵转灰度矩阵
        // 下面检测并显示人脸
        MatOfRect faces = new MatOfRect();
        int absoluteFaceSize = 0;
        int height = gray.rows();
        if (Math.round(height * 0.2f) > 0) {
            absoluteFaceSize = Math.round(height * 0.2f);
        }
        if (mJavaDetector != null) { // 检测器开始识别人脸
            mJavaDetector.detectMultiScale(gray, faces, 1.1, 2, 2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }
        Rect[] faceArray = faces.toArray();
        for (Rect rect : faceArray) { // 给找到的人脸标上相框
            Imgproc.rectangle(rgba, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255), 3);
            Log.d(TAG, rect.toString());
        }
        Bitmap mark = Bitmap.createBitmap(orig.getWidth(), orig.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, mark); // 把Mat结构转为位图对象

        return mark;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.d(TAG, "OpenCV loaded successfully");
                // 在OpenCV初始化完成后加载so库
                //System.loadLibrary("detection_based_tracker");
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                // 从应用程序资源加载级联文件
                try (InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                     FileOutputStream os = new FileOutputStream(cascadeFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 根据级联文件创建OpenCV的人脸检测器
                mJavaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
                if (mJavaDetector.empty()) {
                    Log.d(TAG, "Failed to load cascade classifier");
                    mJavaDetector = null;
                } else {
                    Log.d(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
                }
                cascadeDir.delete();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

}
