package com.xixia.aiimageupload.opcv;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.xixia.aiimageupload.PictureSelectorUtils;
import com.xixia.aiimageupload.R;
import com.xixia.aiimageupload.icc.ICCActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.osgi.OpenCVNativeLoader;

import java.nio.ByteBuffer;
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

    public void moreClick(View view){
        startActivity(new Intent(this,IniyializeActivity.class));
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
                Log.e(TAG, "onResult: " + filePath);
                Glide.with(OpenCVActivity.this).load(filePath).into(ivImage1);
            }
        });
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
        Mat dstmat=getOriginMat(bitmap);
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
}
