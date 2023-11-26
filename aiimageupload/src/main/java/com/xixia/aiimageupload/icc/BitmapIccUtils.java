package com.xixia.aiimageupload.icc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;

import com.xixia.aiimageupload.R;

import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName BitmapIccUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-09-16 11:13
 * @Version 1.0
 * @UpdateDate 2023-09-16 11:13
 * @UpdateRemark 更新说明
 */
public class BitmapIccUtils {


    //    public static ColorSpace loadICCColorSpace(int iccResourceId) {
//        try {
//            // 从资源中加载ICC文件
//            InputStream inputStream= Resources.getSystem().openRawResource(iccResourceId);
//            // 加载ICC文件
//            ColorSpace.Rgb rgbColorSpace = new ColorSpace.Rgb(
//                    ColorSpace.Named.SRGB,
//                    ColorSpace.ILLUMINANT_D65,
//
//
//            );
//            return rgbColorSpace;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    public void saveIccBitmap() {
        int width = 800;  // 图像宽度
        int height = 600; // 图像高度
        ColorSpace colorSpace = ColorSpace.get(ColorSpace.Named.SRGB); // 选择所需的色彩空间
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED); // 设置绘制颜色
        canvas.drawRect(0, 0, width, height, paint); // 绘制一个红色矩形示例
    }
}
