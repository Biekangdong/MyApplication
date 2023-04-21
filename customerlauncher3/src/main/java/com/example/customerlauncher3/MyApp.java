package com.example.customerlauncher3;

import android.app.Application;

import com.example.customerlauncher3.greendao.DaoManager;

/**
 * @ClassName MyApp
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/4/20 11:54
 * @Version 1.0
 * @UpdateDate 2023/4/20 11:54
 * @UpdateRemark 更新说明
 */
public class MyApp extends Application {
    private static MyApp myApp;
    @Override
    public void onCreate() {
        super.onCreate();
        if (myApp == null) {
            myApp = this;
        }

        DaoManager mManager = DaoManager.getInstance();
        mManager.init(this);
    }
    public static MyApp getInstance() {
        return myApp;
    }
}
