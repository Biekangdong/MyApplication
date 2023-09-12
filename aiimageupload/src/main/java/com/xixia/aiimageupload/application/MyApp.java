package com.xixia.aiimageupload.application;

import android.app.Application;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

/**
 * @ClassName MyApp
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/7/25 16:05
 * @Version 1.0
 * @UpdateDate 2023/7/25 16:05
 * @UpdateRemark 更新说明
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);
        Logger.setDebug(true); // 开启NoHttp调试模式。
        Logger.setTag("aiimageupload"); // 设置NoHttp打印Log的TAG。
    }
}
