package com.example.algorithm;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_IN;
import static android.util.TypedValue.COMPLEX_UNIT_MM;
import static android.util.TypedValue.COMPLEX_UNIT_PT;
import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

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
     * px、dp、sp、pt、in、mm单位转换
     * @param context  上下文
     * @param unit  转换类型
     * @param value 转换值(float)
     * @return 转换单位后的值
     */
    public static float applyDimension(Context context,int unit, float value) {
        //当前设备显示密度
        DisplayMetrics metrics=context.getResources().getDisplayMetrics();
        switch (unit) {
            case COMPLEX_UNIT_PX: // 转换为px(像素)值
                return value;
            case COMPLEX_UNIT_DIP: // 转换为dp(密度)值
                return value * metrics.density;
            case COMPLEX_UNIT_SP: // 转换为sp(与刻度无关的像素)值
                return value * metrics.scaledDensity;
            case COMPLEX_UNIT_PT: // 转换为pt(磅)值
                return value * metrics.xdpi * (1.0f / 72);
            case COMPLEX_UNIT_IN: // 转换为in(英寸)值
                return value * metrics.xdpi;
            case COMPLEX_UNIT_MM: // 转换为mm(毫米)值
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }
}
