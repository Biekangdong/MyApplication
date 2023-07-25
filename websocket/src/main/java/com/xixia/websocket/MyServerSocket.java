package com.xixia.websocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @ClassName MyServerSocket
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/7/9 18:49
 * @Version 1.0
 * @UpdateDate 2023/7/9 18:49
 * @UpdateRemark 更新说明
 */
public class MyServerSocket extends Thread{
    private final static String TAG = "MyServerSocket";
    private ServerSocket mServerSocket = null;
    private Socket mSocket = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    private Boolean isConnected = false;
    private int mPort = 12345;
    private Handler mHandler = null;

    public final static int MSG_SOCKET_ACCEPTFAIL=0;
    public final static int MSG_SOCKET_CONNECTOK=1;
    public final static int MSG_SOCKET_READ=2;


    MyServerSocket(int port, Handler handler) {
        mPort = port;
        mHandler = handler;
    }

    @Override
    public void run() {
        System.out.println("MyServerSocket: " + Thread.currentThread().getName());

        try {
            mServerSocket = new ServerSocket(mPort);

            //等待客户端的连接，Accept会阻塞，直到建立连接，
            //所以需要放在子线程中运行。
            Log.d(TAG, "run: start0");
            mSocket = mServerSocket.accept();
            Log.d(TAG, "run: start1");

            //获取输入流
            mInStream = mSocket.getInputStream();
            //获取输出流
            mOutStream = mSocket.getOutputStream();

            isConnected = true;
            writeMsgInternal("### A message from MyServerSocket.");
            readMsgInternal();

        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_SOCKET_ACCEPTFAIL);
            isConnected = false;
            Log.d(TAG, "run: err");
        }
        //Log.i(TAG, "accept success");
        mHandler.sendEmptyMessage(MSG_SOCKET_CONNECTOK);
        Log.d(TAG, "run: end");
    }

    private void readMsgInternal() {
        while (isConnected) {
            byte[] buffer = new byte[1024];
            //循环执行read，用来接收数据。
            //数据存在buffer中，count为读取到的数据长度。
            try {
                Log.d(TAG, "readMsgInternal:start0");
                int count = mInStream.read(buffer);
                String str = new String(buffer, "UTF-8");
                Log.d(TAG, "readMsgInternal:start1");

                Message msg = new Message();
                msg.what = MSG_SOCKET_READ;
                msg.obj = str;
                if(mHandler != null) mHandler.sendMessage ( msg );
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "readMsgInternal:err");
                isConnected = false;
            }
        }

        Log.d(TAG, "readMsgInternal:end");
    }

    private void writeMsgInternal(String msg){
        if(msg.length() == 0 || mOutStream == null)
            return;

        try {   //发送
            mOutStream.write(msg.getBytes());
            mOutStream.flush();
        }catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
        }
    }

    public void writeMsg(String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeMsgInternal(str);
            }
        }).start();
    }

    public void close() {
        isConnected = false;

        if(mServerSocket != null){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mServerSocket = null;
        }

        if(mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }
}

