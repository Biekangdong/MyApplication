package com.dinghe.servicetest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * @ClassName TestMyBinderService
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/15 11:54
 * @Version 1.0
 * @UpdateDate 2023/5/15 11:54
 * @UpdateRemark 更新说明
 */
public class TestMyBinderService extends Service {
    //创建字符串变量
   private String name;

    public String getName() {
        return name;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化字符串变量，后面用于组件通信
        name="嘿嘿哈哈哈";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //绑定服务
        return new MyServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //解绑服务
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 用于客户端绑定器的类。因为我们知道这个服务总是运行在与其客户机相同的进程中，
     * 所以我们不需要处理IPC。
     */
    public class MyServiceBinder extends Binder {
        public TestMyBinderService getService(){
            //返回MyBinderService实例，以便客户端可以调用公共方法
            return TestMyBinderService.this;
        }
    }

}
