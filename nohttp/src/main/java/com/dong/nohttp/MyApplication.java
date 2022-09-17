package com.dong.nohttp;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;

/**
 * @ClassName MyApplication
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2022/7/9 15:24
 * @Version 1.0
 * @UpdateDate 2022/7/9 15:24
 * @UpdateRemark 更新说明
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);
    }
}
