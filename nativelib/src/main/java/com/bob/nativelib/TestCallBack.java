package com.bob.nativelib;

/**
 * @ClassName TestCallBack
 * @Description TODO JNI调用java方法
 * @Author biekangdong
 * @CreateDate 2023/3/7 10:53
 * @Version 1.0
 * @UpdateDate 2023/3/7 10:53
 * @UpdateRemark 更新说明
 */
public class TestCallBack {
    static {
        System.loadLibrary("jnitojava");
    }
    //回调方法 里面调用了add
    public native void callBackAdd();

    public int add(int x,int y){
        return x+y;
    }
}
