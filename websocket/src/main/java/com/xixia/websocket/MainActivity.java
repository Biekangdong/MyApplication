package com.xixia.websocket;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class MainActivity extends AppCompatActivity {
    private WebSocket mWebSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void openSocket(View view) {
        initSocket();
    }


    private void initSocket() {
        String sw_url = "ws://192.168.81.1:1000";
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(sw_url).build();
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {//开启长连接成功的回调
                super.onOpen(webSocket, response);
                mWebSocket = webSocket;
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {//接收消息的回调
                super.onMessage(webSocket, text);
                //收到服务器端传过来的消息text
                Log.e("TAG", "接收消息的回调--------------" + text);
                try {
                    //这个是解析你的回调数据
                    JSONObject jsonObject = new JSONObject(text);
                    //通过事件总线修改
                    String type = jsonObject.getString("type");
                    String message = jsonObject.getString("message");
                    Log.e("TAG", "message：" + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.e("TAG", "onMessage:" + bytes.toString());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.e("TAG", "onClosing:" + reason);

            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.e("TAG", "onClosed:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {//长连接连接失败的回调
                super.onFailure(webSocket, t, response);
                Log.e("TAG", "onFailure:" + response);

            }
        });
        client.dispatcher().executorService().shutdown();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
    }



    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                initSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 心跳检测时间
     */
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测

    private long sendTime = 0L;
    // 发送心跳包
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                if (mWebSocket != null) {
                    String msg="android-心跳";
                    try {
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("id","111");
                        jsonObject.put("title","EEEEEEEEEEEEEEEE");
                        msg=jsonObject.toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    boolean isSuccess = mWebSocket.send(msg);//发送一个消息给服务器，通过发送消息的成功失败来判断长连接的连接状态
                    if (!isSuccess) {//长连接已断开，
                        Log.e("TAG", "发送心跳包-------------长连接已断开");
                        mHandler.removeCallbacks(heartBeatRunnable);
                        mWebSocket.cancel();//取消掉以前的长连接
                        new InitSocketThread().start();//创建一个新的连接
                    } else {//长连接处于连接状态---
                        Log.e("TAG", "发送心跳包-------------长连接处于连接状态");
                    }
                }

                sendTime = System.currentTimeMillis();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
        }
    };

    //关闭长连接
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebSocket != null) {
            mWebSocket.close(1000, null);
        }
    }

}