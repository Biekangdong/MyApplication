package com.dinghe.servicetest.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * @ClassName TestIntentService
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/15 16:08
 * @Version 1.0
 * @UpdateDate 2023/5/15 16:08
 * @UpdateRemark 更新说明
 */
public class TestIntentService extends IntentService {
    private static final String TAG = "TestIntentService";

    @Override
    public void onCreate() {
        super.onCreate();
        //8.0版本开始，启动前台服务之后，必须在5秒之内执行 startForeground 方法
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId="newChannelId";
            String channelName="channelName";
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_LOW).build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 构造函数
     * @deprecated
     */
    public TestIntentService() {
        //传入字符串，标识IntentService子线程name
        super("newIntentService");
        Log.e(TAG, "TestIntentService");
    }

    /**
     * 处理耗时业务
     *
     * @param intent 接收传进来的参数
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //intent获取任务分类
        String action = intent.getAction();
        switch (action) {
            case "com.test.task1":
                try {
                    //耗时任务
                    //.....................
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "onHandleIntent: task1 finished !");
                break;

            case "com.test.task2":
                //耗时任务
                //.....................
                Log.e(TAG, "onHandleIntent: task2 finished !");
                break;
        }
    }

    /**
     * 耗时任务执行完毕之后自动销毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}
