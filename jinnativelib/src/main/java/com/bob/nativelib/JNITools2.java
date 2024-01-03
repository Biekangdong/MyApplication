package com.bob.nativelib;

/**
 * @ClassName JNITools
 * @Description TODO 动态注册
 * @Author biekangdong
 * @CreateDate 2023/3/6 18:00
 * @Version 1.0
 * @UpdateDate 2023/3/6 18:00
 * @UpdateRemark 更新说明
 */
public class JNITools2 {
    static {
        System.loadLibrary("dynamicnativelib2");
    }

    //加法
    public static native int  add(int a,int b);

//    //减法
//    public static native int sub(int a,int b);
//
//    //乘法
//    public static native int mul(int a,int b);
//
//    //除法
//    public static native int div(int a,int b);
}
