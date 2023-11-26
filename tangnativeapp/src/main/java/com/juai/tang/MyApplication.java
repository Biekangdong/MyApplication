package com.juai.tang;

import android.app.Application;

import com.yanzhenjie.nohttp.NoHttp;


/**
 * @ClassName MyApplication
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-09-15 17:13
 * @Version 1.0
 * @UpdateDate 2023-09-15 17:13
 * @UpdateRemark 更新说明
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NoHttp.initialize(this);

    }
}
