package com.system.signalr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.microsoft.signalr.OnClosedCallback;
import com.microsoft.signalr.TransportEnum;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnConnect;
    private Button btnOpt;
    private TextView tvMessage;

    private HubConnection hubConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnOpt = (Button) findViewById(R.id.btn_opt);
        tvMessage = (TextView) findViewById(R.id.tv_message);

        initHubConnection();
//        initWebSocket();
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getConnectionState() == HubConnectionState.DISCONNECTED) {
                    connect();
                }else {
                    stop();
                    btnConnect.setText("连接");
                }
            }
        });

        btnOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OptActivity.class));
            }
        });
    }



//    private void initWebSocket(){
//        //初始化
//        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiOTEiLCJ1c2VyX25hbWUiOiI5MS0xODIzNjE2MTA5NyIsInNjb3BlIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiLCJST0xFX0FQSSJdLCJleHAiOjE2NTA1NzQzMTQsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiI2ZGQwMWYzYi1kNjVkLTQ2YWQtOTU4NS03YTA3ZTUwYjIwNWQiLCJjbGllbnRfaWQiOiJqdWFpLWxpZmUiLCJjZWxsX3Bob25lIjoiMTgyMzYxNjEwOTcifQ.T_f6BkdJ2HElLzWV4Ht8MV0uxjuFOkybAEHmhdf8tfs";
//        String url = "https://singnalr-union.juaiyouxuan.com/MessagetHub";//长连接地址
//
//        Map<String, String> httpHeader=new HashMap<>();
//        httpHeader.put("Authorization","Bearer "+token);
//        WebSocketClient webSocketClient=new WebSocketClient(URI.create(url),httpHeader) {
//            @Override
//            public void onOpen(ServerHandshake handshakedata) {
//                Log.e(TAG, "onOpen: "+handshakedata.getHttpStatusMessage());
//            }
//
//            @Override
//            public void onMessage(String message) {
//                Log.e(TAG, "onMessage: "+message);
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                Log.e(TAG, "onClose: "+reason);
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                Log.e(TAG, "onError: "+ex.getMessage());
//            }
//        };
//        webSocketClient.connect();
//        webSocketClient.setConnectionLostTimeout(1000);
//    }
//
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//       stop();
//    }



    public HubConnectionState getConnectionState(){
        if(hubConnection!=null){
            hubConnection.getConnectionState();
            return hubConnection.getConnectionState();
        }
        return HubConnectionState.DISCONNECTED;

    }


    public void initHubConnection() {
        //初始化
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiOTEiLCJ1c2VyX25hbWUiOiI5MS0xODIzNjE2MTA5NyIsInNjb3BlIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiLCJST0xFX0FQSSJdLCJleHAiOjE2NTA2NTQzMzksImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiI4MDQ0MjkxMC0yMDcyLTRiMjEtYTQ5ZC0wNzg0NjVlMDAzZGUiLCJjbGllbnRfaWQiOiJqdWFpLWxpZmUiLCJjZWxsX3Bob25lIjoiMTgyMzYxNjEwOTcifQ.XRNaM_eIxTqWwnI-3MGhCPnnA4NE5BKH5poxcA3RfwY";
        String url = "https://singnalr-union.juaiyouxuan.com/MessagetHub";//长连接地址
        hubConnection = HubConnectionBuilder.create(url)
                .withAccessTokenProvider(Single.defer(() -> {
                    // Your logic here.
                    return Single.just(token);
                }))
//                .withHeader("Authorization", "Bearer " +token)
                .withTransport(TransportEnum.LONG_POLLING)
                .build();

        hubConnection.setKeepAliveInterval(2*1000);//心跳


        //接收消息方法设置
        hubConnection.on("ReceiveMessage", (type, json) -> {
//            stringBuffer.append(type + json).append("\n\n");
//            if(onHubListener!=null){
//                onHubListener.onReceiveListener(stringBuffer.toString());
//            }
            Log.e(TAG, "hubConnection SDKSig:New type: " + type + ",json: " + json);

        }, String.class, String.class);

        //连接关闭状态监听
        hubConnection.onClosed(new OnClosedCallback() {
            @Override
            public void invoke(Exception exception) {

            }
        });

    }

    //进行连接
    public void connect() {
        Log.e(TAG, "connect:");
        new HubConnectionTask().execute(hubConnection);
    }

    class HubConnectionTask extends AsyncTask<HubConnection, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(HubConnection... hubConnections) {
            HubConnection hubConnection = hubConnections[0];
            //进行连接
            try {
                hubConnection.start().blockingAwait(); //进行连接
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "fail:" + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.e(TAG, "onPostExecute: "+aBoolean);
            tvMessage.setText(aBoolean?"连接成功":"连接失败");

        }
    }

//    //发送数据
//    public void sendMessage(String SnStr) {
//        hubConnection.invoke("Login", "android", SnStr, token);
//    }

    public void stop(){
        //关闭连接
        if (hubConnection != null) {
            hubConnection.stop();
        }
    }
}