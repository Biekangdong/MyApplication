package com.dinghe.servicetest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dinghe.servicetest.R;
import com.dinghe.servicetest.receiver.MyOrderBroadcastReceiver;
import com.dinghe.servicetest.receiver.MyReceiver;
import com.dinghe.servicetest.service.TestJobService;
import com.dinghe.servicetest.service.TestMyBinderService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MyConn myConn;

    MyOrderBroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myConn = new MyConn();
        Intent intent = new Intent(this, TestMyBinderService.class);
        bindService(intent, myConn, BIND_AUTO_CREATE);

        myReceiver = new MyOrderBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1);
        intentFilter.addAction("BROADCAST_ACTION1");
        registerReceiver(myReceiver, intentFilter);


        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.setPriority(2);
        intentFilter2.addAction("BROADCAST_ACTION2");
        registerReceiver(myReceiver, intentFilter2);

    }

    private class MyConn implements ServiceConnection {

        //当服务连接成功调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取中间人对象
            TestMyBinderService.MyServiceBinder myBinder = (TestMyBinderService.MyServiceBinder) service;
            TestMyBinderService testMyBinderService = myBinder.getService();

            String nameValue = testMyBinderService.getName();
            Log.e(TAG, "onServiceConnected: " + nameValue);

        }

        //失去连接
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    public void toActivity3(View view) {

//        Intent intent = new Intent(this, TestIntentService.class);
//        intent.setAction("com.test.task1");
//        startService(intent);

//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, TestJobService.class));  //指定哪个JobService执行操作
//        jobScheduler.schedule(builder.build());

        int jobId = 1;
        JobScheduler jobScheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName jobService = new ComponentName(getPackageName(),
                TestJobService.class.getName());
        JobInfo jobInfo = new JobInfo.Builder(jobId,jobService)
                .setMinimumLatency(1000)//延时
                //.setOverrideDeadline(1000)//若失效，定时
                //.setPeriodic(15 * 60 * 1000)//任务执行周期
                .setPersisted(true)//设备重启后是否继续执行
                .setRequiresCharging(true)//设置是否需要充电
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)//网络条件,任意网络，默认NETWORK_TYPE_NONE
                .build();
        if(jobScheduler != null){
            jobScheduler.schedule(jobInfo);
        }
//        jobScheduler.cancel(0);
//        jobScheduler.cancelAll();

//        buttonClick();
    }


    private void buttonClick() {
//        Intent intent = new Intent(this, MyReceiver.class);
//        sendBroadcast(intent);

        Intent intent0 = new Intent();
        intent0.setAction("BROADCAST_ACTION1");
        intent0.putExtra("msg", "你好啊ww");
        sendOrderedBroadcast(intent0, null);

        Intent intent = new Intent();
        intent.setAction("BROADCAST_ACTION2");
        intent.putExtra("msg", "你好啊");
        sendOrderedBroadcast(intent, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
    }

    private void insertData() {
        String name = "张三";
        String phone = "123456789101";

        ContentValues values = new ContentValues();
        Uri uri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContentID = ContentUris.parseId(uri);
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContentID);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);

        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

    }

    private void updateData() {
        String name_update = "张三";
        String phone_update = "123456789101";

        Long rawContactId = 0L;
        ContentValues valuesUpdate = new ContentValues();
        valuesUpdate.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone_update);

        Cursor cursorUpdate = getContentName(name_update);
        if (cursorUpdate.moveToFirst()) {
            rawContactId = cursorUpdate.getLong(0);
        }
        getContentResolver().update(ContactsContract.Data.CONTENT_URI, valuesUpdate, "raw_contact_id=?", new String[]{rawContactId + ""});
        cursorUpdate.close();
    }

    //根据名字查询
    private Cursor getContentName(String name_search) {
        String[] query_all = new String[]{
                ContactsContract.CommonDataKinds.Identity.RAW_CONTACT_ID, //用户id
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //联系人姓名
                ContactsContract.CommonDataKinds.Phone.NUMBER //联系人电话
        };

        String selections = ContactsContract.Contacts.DISPLAY_NAME + "=?";
        String[] selection_args = new String[]{name_search};
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, query_all, selections, selection_args, null);
        return cursor;
    }

    private void deleteData() {
        String name1 = "张三";
        int count = getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.Contacts.DISPLAY_NAME + "=?", new String[]{name1});
        if (count > 0) {
            Toast.makeText(this, "删除成功！", Toast.LENGTH_SHORT).show();
        }
    }
}