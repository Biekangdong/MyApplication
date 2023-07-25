package com.dinghe.servicetest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @ClassName MyReceiver
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/16 10:08
 * @Version 1.0
 * @UpdateDate 2023/5/16 10:08
 * @UpdateRemark 更新说明
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg=intent.getStringExtra("msg");
        Log.e(TAG, "onReceive: "+msg);
    }
}
