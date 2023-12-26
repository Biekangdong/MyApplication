package com.xixia.aiimageupload.camera2;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * @ClassName MainActivity
 * @Description TODO 相机扫码配置
 * @Author biekangdong
 * @CreateDate 2022/6/1 11:21
 * @Version 1.0
 * @UpdateDate 2022/6/1 11:21
 * @UpdateRemark 更新说明
 */
public class ScanConfig {
    public static final int PPI_640_480 = 1;
    public static final int PPI_160_120 = 2;
    public static final int PPI_176_144 = 3;
    public static final int PPI_320_240 = 4;
    public static final int PPI_352_288 = 5;


    //当前分辨率
    public static int CURRENT_PPI = PPI_640_480;
    //扫描完成声音提示
    public static boolean PLAY_SOUND = true;
    //扫描完成震动
    public static boolean PLAY_VIBRATE = false;
    //识别反色二维码
    public static boolean IDENTIFY_INVERSE_QR_CODE = true;
    //识别画面中多个二维码
    public static boolean IDENTIFY_MORE_CODE = false;
    //是否显示设置按钮
    public static boolean IS_SHOW_SETTING = true;
    //是否显示选择相册按钮
    public static boolean IS_SHOW_ALBUM = true;

    //灯模式: false 灯灭; true 灯亮
    public static boolean IS_OPEN_LIGHT = false;

    //灯亮时间（单位: 毫秒）
    public static int LIGHT_BRIGHT_TIME = 200;

    //灯灭时间（单位: 毫秒）
    public static int LIGHT_DROWN_TIME = 500;

    //扫码模式: false 单次扫码; true 循环扫码
    public static boolean SCAN_MODE = false;

    //灯索引: 0 nfc灯; 1 camera灯; 其它错误
    public static int LIGHT_INDEX = 1;

    //扫描结果的数据的键
    public static final String TYPE = "TYPE";//扫描码的类型
    public static final String VALUE = "VALUE";//扫描码的数据

    public static Point BEST_RESOLUTION ;

    /**
     * @Author yocn
     * @Date 2019/8/5 4:11 PM
     * @ClassName BitmapUtil
     */
    public static class BitmapUtil {
        public static Bitmap rotateBitmap(Bitmap origin, float rotate) {
            if (origin == null) {
                return null;
            }
            int width = origin.getWidth();
            int height = origin.getHeight();
            Matrix matrix = new Matrix();
            matrix.setRotate(rotate);
            // 围绕原地进行旋转
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            if (newBM.equals(origin)) {
                return newBM;
            }
            origin.recycle();
            return newBM;
        }

        public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
            byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
            // Rotate the Y luma
            int i = 0;
            for (int x = 0; x < imageWidth; x++) {
                for (int y = imageHeight - 1; y >= 0; y--) {
                    yuv[i] = data[y * imageWidth + x];
                    i++;
                }
            }
            // Rotate the U and V color components
            i = imageWidth * imageHeight * 3 / 2 - 1;
            for (int x = imageWidth - 1; x > 0; x = x - 2) {
                for (int y = 0; y < imageHeight / 2; y++) {
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                    i--;
                    yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                    i--;
                }
            }
            return yuv;
        }

        public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
            YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
            byte[] jdata = baos.toByteArray();
            BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
            bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
            return bmp;
        }

        public static byte[] convertColorToByte(int color[]) {
            if (color == null) {
                return null;
            }

            byte[] data = new byte[color.length * 3];
            for (int i = 0; i < color.length; i++) {
                data[i * 3] = (byte) (color[i] >> 16 & 0xff);
                data[i * 3 + 1] = (byte) (color[i] >> 8 & 0xff);
                data[i * 3 + 2] = (byte) (color[i] & 0xff);
            }

            return data;
        }

        public static void dumpFile(String fileName, byte[] data) {
            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(fileName);
            } catch (IOException ioe) {
                throw new RuntimeException("Unable to create output file " + fileName, ioe);
            }
            try {
                outStream.write(data);
                outStream.close();
            } catch (IOException ioe) {
                throw new RuntimeException("failed writing data to file " + fileName, ioe);
            }
        }

        /**
         * I420转nv21
         */
        public static byte[] I420Tonv21(byte[] data, int width, int height) {
            byte[] ret = new byte[data.length];
            int total = width * height;

            ByteBuffer bufferY = ByteBuffer.wrap(ret, 0, total);
            ByteBuffer bufferVU = ByteBuffer.wrap(ret, total, total / 2);

            bufferY.put(data, 0, total);
            for (int i = 0; i < total / 4; i += 1) {
                bufferVU.put(data[i + total + total / 4]);
                bufferVU.put(data[total + i]);
            }

            return ret;
        }

        public static void saveBitmap(String path, Bitmap bitmap) {
            //获取文件
            File file = new File(path);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static Bitmap getBitmapFromAssets(Context context, String path) {
            Bitmap bitmap = null;
            AssetManager am = context.getResources().getAssets();
            try {
                //读取assert 的文图
                InputStream is = am.open(path);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
            }
            return bitmap;
        }

    }

    /**
     * @Author yocn
     * @Date 2019/8/5 5:03 PM
     * @ClassName YuvToRGB
     */
    public static class YuvToRGB {
        private static int R = 0;
        private static int G = 1;
        private static int B = 2;

        //I420是yuv420格式，是3个plane，排列方式为(Y)(U)(V)
        public static int[] I420ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int positionOfV = numOfPixel;
            int positionOfU = numOfPixel / 4 + numOfPixel;
            int[] rgb = new int[numOfPixel * 3];
            for (int i = 0; i < height; i++) {
                int startY = i * width;
                int step = (i / 2) * (width / 2);
                int startU = positionOfV + step;
                int startV = positionOfU + step;
                for (int j = 0; j < width; j++) {
                    int Y = startY + j;
                    int U = startU + j / 2;
                    int V = startV + j / 2;
                    int index = Y * 3;
                    RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }

            return rgb;
        }

        private static class RGB {
            public int r, g, b;
        }

        private static RGB yuvTorgb(byte Y, byte U, byte V) {
            RGB rgb = new RGB();
            rgb.r = (int) ((Y & 0xff) + 1.4075 * ((V & 0xff) - 128));
            rgb.g = (int) ((Y & 0xff) - 0.3455 * ((U & 0xff) - 128) - 0.7169 * ((V & 0xff) - 128));
            rgb.b = (int) ((Y & 0xff) + 1.779 * ((U & 0xff) - 128));
            rgb.r = (rgb.r < 0 ? 0 : rgb.r > 255 ? 255 : rgb.r);
            rgb.g = (rgb.g < 0 ? 0 : rgb.g > 255 ? 255 : rgb.g);
            rgb.b = (rgb.b < 0 ? 0 : rgb.b > 255 ? 255 : rgb.b);
            return rgb;
        }

        //YV16是yuv422格式，是三个plane，(Y)(U)(V)
        public static int[] YV16ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int positionOfU = numOfPixel;
            int positionOfV = numOfPixel / 2 + numOfPixel;
            int[] rgb = new int[numOfPixel * 3];
            for (int i = 0; i < height; i++) {
                int startY = i * width;
                int step = i * width / 2;
                int startU = positionOfU + step;
                int startV = positionOfV + step;
                for (int j = 0; j < width; j++) {
                    int Y = startY + j;
                    int U = startU + j / 2;
                    int V = startV + j / 2;
                    int index = Y * 3;
                    //rgb[index+R] = (int)((src[Y]&0xff) + 1.4075 * ((src[V]&0xff)-128));
                    //rgb[index+G] = (int)((src[Y]&0xff) - 0.3455 * ((src[U]&0xff)-128) - 0.7169*((src[V]&0xff)-128));
                    //rgb[index+B] = (int)((src[Y]&0xff) + 1.779 * ((src[U]&0xff)-128));
                    RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //YV12是yuv420格式，是3个plane，排列方式为(Y)(V)(U)
        public static int[] YV12ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int positionOfV = numOfPixel;
            int positionOfU = numOfPixel / 4 + numOfPixel;
            int[] rgb = new int[numOfPixel * 3];

            for (int i = 0; i < height; i++) {
                int startY = i * width;
                int step = (i / 2) * (width / 2);
                int startV = positionOfV + step;
                int startU = positionOfU + step;
                for (int j = 0; j < width; j++) {
                    int Y = startY + j;
                    int V = startV + j / 2;
                    int U = startU + j / 2;
                    int index = Y * 3;

                    //rgb[index+R] = (int)((src[Y]&0xff) + 1.4075 * ((src[V]&0xff)-128));
                    //rgb[index+G] = (int)((src[Y]&0xff) - 0.3455 * ((src[U]&0xff)-128) - 0.7169*((src[V]&0xff)-128));
                    //rgb[index+B] = (int)((src[Y]&0xff) + 1.779 * ((src[U]&0xff)-128));
                    RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //YUY2是YUV422格式，排列是(YUYV)，是1 plane
        public static int[] YUY2ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int[] rgb = new int[numOfPixel * 3];
            int lineWidth = 2 * width;
            for (int i = 0; i < height; i++) {
                int startY = i * lineWidth;
                for (int j = 0; j < lineWidth; j += 4) {
                    int Y1 = j + startY;
                    int Y2 = Y1 + 2;
                    int U = Y1 + 1;
                    int V = Y1 + 3;
                    int index = (Y1 >> 1) * 3;
                    RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                    index += 3;
                    tmp = yuvTorgb(src[Y2], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //UYVY是YUV422格式，排列是(UYVY)，是1 plane
        public static int[] UYVYToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int[] rgb = new int[numOfPixel * 3];
            int lineWidth = 2 * width;
            for (int i = 0; i < height; i++) {
                int startU = i * lineWidth;
                for (int j = 0; j < lineWidth; j += 4) {
                    int U = j + startU;
                    int Y1 = U + 1;
                    int Y2 = U + 3;
                    int V = U + 2;
                    int index = (U >> 1) * 3;
                    RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                    index += 3;
                    tmp = yuvTorgb(src[Y2], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //NV21是YUV420格式，排列是(Y), (VU)，是2 plane
        public static int[] NV21ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int positionOfV = numOfPixel;
            int[] rgb = new int[numOfPixel * 3];

            for (int i = 0; i < height; i++) {
                int startY = i * width;
                int step = i / 2 * width;
                int startV = positionOfV + step;
                for (int j = 0; j < width; j++) {
                    int Y = startY + j;
                    int V = startV + j / 2;
                    int U = V + 1;
                    int index = Y * 3;
                    RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //NV12是YUV420格式，排列是(Y), (UV)，是2 plane
        public static int[] NV12ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int positionOfU = numOfPixel;
            int[] rgb = new int[numOfPixel * 3];

            for (int i = 0; i < height; i++) {
                int startY = i * width;
                int step = i / 2 * width;
                int startU = positionOfU + step;
                for (int j = 0; j < width; j++) {
                    int Y = startY + j;
                    int U = startU + j / 2;
                    int V = U + 1;
                    int index = Y * 3;
                    RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //NV16是YUV422格式，排列是(Y), (UV)，是2 plane
        public static int[] NV16ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int positionOfU = numOfPixel;
            int[] rgb = new int[numOfPixel * 3];

            for (int i = 0; i < height; i++) {
                int startY = i * width;
                int step = i * width;
                int startU = positionOfU + step;
                for (int j = 0; j < width; j++) {
                    int Y = startY + j;
                    int U = startU + j / 2;
                    int V = U + 1;
                    int index = Y * 3;
                    RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //NV61是YUV422格式，排列是(Y), (VU)，是2 plane
        public static int[] NV61ToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int positionOfV = numOfPixel;
            int[] rgb = new int[numOfPixel * 3];

            for (int i = 0; i < height; i++) {
                int startY = i * width;
                int step = i * width;
                int startV = positionOfV + step;
                for (int j = 0; j < width; j++) {
                    int Y = startY + j;
                    int V = startV + j / 2;
                    int U = V + 1;
                    int index = Y * 3;
                    RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //YVYU是YUV422格式，排列是(YVYU)，是1 plane
        public static int[] YVYUToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int[] rgb = new int[numOfPixel * 3];
            int lineWidth = 2 * width;
            for (int i = 0; i < height; i++) {
                int startY = i * lineWidth;
                for (int j = 0; j < lineWidth; j += 4) {
                    int Y1 = j + startY;
                    int Y2 = Y1 + 2;
                    int V = Y1 + 1;
                    int U = Y1 + 3;
                    int index = (Y1 >> 1) * 3;
                    RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                    index += 3;
                    tmp = yuvTorgb(src[Y2], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }

        //VYUY是YUV422格式，排列是(VYUY)，是1 plane
        public static int[] VYUYToRGB(byte[] src, int width, int height) {
            int numOfPixel = width * height;
            int[] rgb = new int[numOfPixel * 3];
            int lineWidth = 2 * width;
            for (int i = 0; i < height; i++) {
                int startV = i * lineWidth;
                for (int j = 0; j < lineWidth; j += 4) {
                    int V = j + startV;
                    int Y1 = V + 1;
                    int Y2 = V + 3;
                    int U = V + 2;
                    int index = (U >> 1) * 3;
                    RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                    index += 3;
                    tmp = yuvTorgb(src[Y2], src[U], src[V]);
                    rgb[index + R] = tmp.r;
                    rgb[index + G] = tmp.g;
                    rgb[index + B] = tmp.b;
                }
            }
            return rgb;
        }
    }
}
