package com.juai.usbmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    private UsbManager manager;
    private UsbDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void openUsbClick(View view) {
        initUsbManager();
    }

    /**
     * 初始化UsbManager
     */
    private void initUsbManager() {
        //获取UsbManager
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        //查找设备
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e(TAG, "onCreate: " + device.getDeviceName());
            requestPermission(device);
        }


    }

    /**
     * 请求权限
     */
    private void requestPermission(UsbDevice device) {
        //请求权限
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);
        manager.requestPermission(device, permissionIntent);
    }

    /**
     * 权限回调
     */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            openDevice();
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    /**
     * 打开设备，接口数据
     */
    private static int TIMEOUT = 100;
    private boolean forceClaim = true;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private   UsbEndpoint usbEndpoint;
    private void openDevice() {
        if (connection == null) {
            //打开设备
            connection = manager.openDevice(device);
            // 配置设备接口
            usbInterface = device.getInterface(0);
            connection.claimInterface(usbInterface, forceClaim);
            // 获取设备端点
            usbEndpoint= usbInterface.getEndpoint(0);
            readData();
        }



        //发送数据
        //byte[] dataToSend = "aa".getBytes(); // 需要发送的数据
        //int bytesSent = connection.bulkTransfer(usbEndpoint, dataToSend, dataToSend.length, TIMEOUT);


    }

    //读取数据
    boolean isReading = true;
    private void readData(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (isReading){
                    // 接收数据
                    byte[] buffer = new byte[1024];
                    int byteCount = connection.bulkTransfer(usbEndpoint, buffer, buffer.length, 1000);
                    if (byteCount > 0) {
                        // 读取到有效数据
                        String data = new String(buffer, 0, byteCount);
                        // 处理数据
                        Log.e(TAG, "data: "+data );
                    }
                }
            }
        }).start();
    }

    /**
     * 关闭设备
     */
    public void closeConnect() {
        if (connection != null) {
            connection.releaseInterface(usbInterface);
            connection.close();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        closeConnect();
//    }

}