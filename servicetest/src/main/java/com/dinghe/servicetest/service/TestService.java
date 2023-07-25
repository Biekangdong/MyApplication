package com.dinghe.servicetest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * @ClassName TestService
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/15 11:17
 * @Version 1.0
 * @UpdateDate 2023/5/15 11:17
 * @UpdateRemark 更新说明
 */
public class TestService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
