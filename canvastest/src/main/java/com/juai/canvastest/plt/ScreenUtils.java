package com.juai.canvastest.plt;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author：luck
 * @date：2017-5-30 19:30
 * @describe：ScreenUtils
 */
public class ScreenUtils {
    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue 字体的大小
     * @return
     */
    public static float sp2px(Context context, float spValue) {
        //fontScale （DisplayMetrics类中属性scaledDensity）
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels - getStatusBarHeight(context);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result == 0 ? dip2px(context, 25) : result;
    }


    /**
     * 将float转化为dip
     * @param context
     * @param px
     * @return
     */
    public static int px2dip(Context context, float px){
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }


    /**
     * 像素=毫米x分辨率
     * dip，像素/英寸单位，1英寸=2.54厘米=25.4毫米
     * metrics.xdpi * (1.0f/25.4f)  代表分辨率x1.0fx1英寸  就是所需的dip(25.4f毫米级表示1英寸)
     * (300f / 25.4f) 一英寸上有300像素，一毫米上有 (300f / 25.4f)像素
     * value 毫米值
     */
    public static float getApplyDimension(int dipValue,float value) {
        return value * dipValue * (1f / 25.4f);
    }
}
