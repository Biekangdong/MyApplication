package com.dinghe.servicetest.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @ClassName MyOrderBroadcastReceiver
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/16 11:00
 * @Version 1.0
 * @UpdateDate 2023/5/16 11:00
 * @UpdateRemark 更新说明
 */
public class MyOrderBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyOrderBroadcastReceive";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        String msg=intent.getStringExtra("msg");

        switch (action){
            case "BROADCAST_ACTION1":
                Log.e(TAG, "BROADCAST_ACTION1: "+msg );
                break;
            case "BROADCAST_ACTION2":
                Log.e(TAG, "BROADCAST_ACTION2: "+msg );
                //优先级高，先收到消息，可以拦截断开有序广播，不再执行下一广播
                //abortBroadcast();
                break;
        }
    }
}
