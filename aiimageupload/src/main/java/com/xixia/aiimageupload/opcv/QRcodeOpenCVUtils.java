package com.xixia.aiimageupload.opcv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName QRcodeOpenCVUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-10-17 17:17
 * @Version 1.0
 * @UpdateDate 2023-10-17 17:17
 * @UpdateRemark 更新说明
 */
public class QRcodeOpenCVUtils {
    private static final String TAG = "QRcodeOpenCVUtils";
    /**
     * 首先经过图像预处理进行轮廓检测，
     * 通过hierarchy、RETR_TREE找到轮廓之间的层级关系
     * 根据hierarchy[i][2]是否为-1判断该轮廓是否有子轮廓
     * 若该轮廓存在子轮廓，则统计有几个子轮廓
     * 如果该轮廓存在子轮廓，且有2级子轮廓则认定找到‘回’
     */
    public static List<Mat> Find_QR_Rect(Mat src) {
        /**
         * 颜色转换-灰度化
         * image: 原始图像
         * 新灰度图
         * 转换参数：多种转换方式
         */
        Mat cvtMat=new Mat();
        Imgproc.cvtColor(src, cvtMat, Imgproc.COLOR_RGB2HSV);

        //检测颜色
        //Core.inRange(hsvmat, new Scalar(160, 90, 90), new Scalar(179, 255, 255), hsvmat);


        /**
         * 模糊
         * src ： 图片
         * dst ： 目标图
         * ksize ：内核大小 Size(Weight， Height)
         * anchor ： 内核锚点。
         * borderType ： 边界填充方式。默认值填充方式BORDER_DEFAULT
         */
        Mat blurMat=new Mat();
        Imgproc.blur(cvtMat, blurMat, new Size(3, 3));

        /**
         * 二值化
         * src 是输入的函数图像
         * dst 是输出的函数图像
         * thresh 是门槛，当矩阵中的元素值>thresh 取值a; 当小于<thresh取值为b
         * maxval 取值b的时候的最大值
         * type 门槛类型
         */
        Mat thresholdMat=new Mat();
        Imgproc.threshold(blurMat, thresholdMat, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);


        /**
         * 获取结构元素函数
         * shape：结构元素形状
         * Size：确定结构元素大小
         * Point：确定锚点（-1，-1）默认为最中间
         * 通过Size（5，1）开运算消除边缘毛刺
         */
        Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 1));
        /**
         *  开运算和闭运算，降低噪点
         *  闭运算
         *  src：需要处理的图像。
         *  dst：处理结果。
         *  Imgproc.MORPH_OPEN：开运算的标识。
         *  kernel：结构元素。
         */
        Mat morphologyExMat=new Mat();
        Imgproc.morphologyEx(thresholdMat, morphologyExMat, Imgproc.MORPH_OPEN, kernel1);

        /**
         * 获取结构元素函数
         * shape：结构元素形状
         * Size：确定结构元素大小
         * Point：确定锚点（-1，-1）默认为最中间
         * 通过Size（21，1）闭运算能够有效地将矩形区域连接 便于提取矩形区域
         */
        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(21, 1));
        /**
         *  开运算和闭运算，降低噪点
         *  闭运算
         *  src：需要处理的图像。
         *  dst：处理结果。
         *  Imgproc.MORPH_CLOSE：开运算的标识。
         *  kernel：结构元素。
         */
        Mat morphologyExMat2=new Mat();
        Imgproc.morphologyEx(morphologyExMat, morphologyExMat2, Imgproc.MORPH_CLOSE, kernel2);


        /**
         * 使用RETR_EXTERNAL找到最外轮廓
         * src：需要处理的图像。
         * List:找到的轮廓放到内存里面。
         * dst：处理结果。
         * mode:轮廓检索模式
         * method:第五个参数是节点拟合模式，这里是全部寻找
         */
        List<Mat> POIList = new ArrayList<>();//裁剪区域
        List<MatOfPoint> matOfPointList = new ArrayList<>();
        Mat hierarchy=new Mat();
        Imgproc.findContours(morphologyExMat2, matOfPointList, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < matOfPointList.size(); i++) {
            //Rect rect = Imgproc.boundingRect(matOfPointList.get(i));
            //创建一个指定大小和类型的全零矩阵
            //Mat maskMat = Mat.zeros(dstmat.size(), CvType.CV_8UC3);
            //mask = Scalar::all(255);
            Mat maskMat = new Mat(hierarchy.rows(), hierarchy.cols(), CvType.CV_8UC3, Scalar.all(255));

            double area = Imgproc.contourArea(matOfPointList.get(i));

            //通过面积阈值找到二维码所在矩形区域
            if (area > 6000 && area < 100000) {
                //拟合旋转矩形,计算最小外接矩形
                RotatedRect MaxRect = Imgproc.minAreaRect(new MatOfPoint2f(matOfPointList.get(0).toArray()));
                //计算最小外接矩形宽高比
                double ratio = MaxRect.size.width / MaxRect.size.height;

                if (ratio > 0.8 && ratio < 1.2) {
                    Rect MaxBox = MaxRect.boundingRect();

                    //rectangle(src, Rect(MaxBox.tl(), MaxBox.br()), Scalar(255, 0, 255), 2);
                    //将矩形区域从原图抠出来
                    Mat ROI = new Mat(src, new Rect(MaxBox.tl(), MaxBox.br()));
                    ROI.copyTo(new Mat(maskMat, MaxBox));
                    ROI.push_back(maskMat);
                }
            }
            POIList.add(maskMat);
        }
        return POIList;
    }

    //对找到的矩形区域进行识别是否为二维码
    static int  Dectect_QR_Rect(Mat src, Mat canvas, List<Mat> ROI_Rect) {
        //用于存储检测到的二维码
        List<List<Point>> QR_Rect = new ArrayList<>();

        //遍历所有找到的矩形区域
        for (int m = 0; m < ROI_Rect.size(); m++) {
            Mat desMat = ROI_Rect.get(m);
            Imgproc.cvtColor(desMat, desMat, Imgproc.COLOR_BGR2GRAY);

            Imgproc.threshold(desMat, desMat, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

            //通过hierarchy、RETR_TREE找到轮廓之间的层级关系
            List<MatOfPoint> contours = new ArrayList<>();
            /**
             * hierarchy 可选的输出。包含轮廓之间的联系。4通道矩阵，元素个数为轮廓数量。通道【0】~ 通道【3】对应保存：后一个轮廓下标，前一个轮廓下标，父轮廓下标，内嵌轮廓下标。如果没有后一个，前一个，父轮廓，内嵌轮廓，那么该通道的值为 -1。
             */
            Mat hierarchy = new Mat();
            Imgproc.findContours(desMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

            //父轮廓索引
            int ParentIndex = -1;
            int cn = 0;

            //用于存储二维码矩形的三个“回”
            List<Point> rect_points = new ArrayList<>();

            ///////////////////////////////////////////////////////
            System.out.println("轮廓数量：" + contours.size());
            System.out.println("hierarchy类型：" + hierarchy);
            for (int k = 0; k < hierarchy.cols(); k++) {
                System.out.print("轮廓下标：" + k + " { ");
                double[] ds = hierarchy.get(0, k);
                for (int l = 0; l < ds.length; l++) {
                    switch (l) {
                        case 0:
                            System.out.print(" 后一个轮廓下标：" + ds[l]);
                            break;
                        case 1:
                            System.out.print("  前一个轮廓下标：" + ds[l]);
                            break;
                        case 2:
                            System.out.print("  父轮廓下标：" + ds[l]);
                            if (ds[l] != -1 && cn == 0) {
                                ParentIndex = k;
                                cn++;
                            } else if (ds[l] != -1 && cn == 1) {
                                ParentIndex = k;
                                cn++;
                            } else if (ds[l] == -1) {
                                //初始化
                                ParentIndex = -1;
                                cn = 0;
                            }

                            //如果该轮廓存在子轮廓，且有2级子轮廓则认定找到‘回'
                            if (ds[l] != -1 && cn == 2) {
                                Imgproc.drawContours(canvas, contours, ParentIndex, Scalar.all(255), -1);

                                RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(ParentIndex).toArray()));

                                rect_points.add(rect.center);
                            }
                            break;
                        case 3:
                            System.out.print("  内嵌轮廓下标：" + ds[l]);
                            break;

                        default:
                            break;
                    }
                }
                System.out.print(" }\n");


//                for (int j = 0; j < contours.size(); j++)
//                {
//                    //hierarchy[i][2] != -1 表示该轮廓有子轮廓  cn用于计数“回”中第几个轮廓
//                    if (desMat.get(j,2) != -1 && cn == 0)
//                    {
//                        ParentIndex = j;
//                        cn++;
//                    }
//                    else if (hierarchy[j][2] != -1 && cn == 1)
//                    {
//                        cn++;
//                    }
//                    else if (hierarchy[j][2] == -1)
//                    {
//                        //初始化
//                        ParentIndex = -1;
//                        cn = 0;
//                    }
//
//                    //如果该轮廓存在子轮廓，且有2级子轮廓则认定找到‘回'
//                    if (hierarchy[j][2] != -1 && cn == 2)
//                    {
//                        Imgproc.drawContours(canvas, contours, ParentIndex, Scalar.all(255), -1);
//
//                        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(ParentIndex).toArray()));
//
//                        rect_points.add(rect.center);
//                    }
//
//                }
            }
            //////////////////////////////////////////////////////

            //将找到地‘回'连接起来
            for (int i = 0; i < rect_points.size(); i++) {
                Imgproc.line(canvas, rect_points.get(i), rect_points.get((i + 1) % rect_points.size()), Scalar.all(255), 5);
            }

            QR_Rect.add(rect_points);

        }


        return QR_Rect.size();

    }

    public static Bitmap mainQrcodeDecode(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, src);


        if (src.empty()) {
            return null;
        }

        List<Mat> POIList = Find_QR_Rect(src);

        Mat canvas = Mat.zeros(src.size(), src.type());
        int flag = Dectect_QR_Rect(src, canvas, POIList);
        //imshow("canvas", canvas);

        if (flag <= 0) {
            return null;
        }

        Log.e(TAG, "检测到" + flag + "个二维码。");

        //框出二维码所在位置
        Mat gray = new Mat();
        Imgproc.cvtColor(canvas, gray, Imgproc.COLOR_BGR2GRAY);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Point points[]=new Point[4];
        for (int i = 0; i < contours.size(); i++) {
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            rect.points(points);

            for (int j = 0; j < 4; j++) {
                Imgproc.line(src, points[j], points[(j + 1) % 4],new Scalar(0, 255, 0), 2);
            }

        }
        Utils.matToBitmap(src, bitmap);
       return bitmap;
    }
}
