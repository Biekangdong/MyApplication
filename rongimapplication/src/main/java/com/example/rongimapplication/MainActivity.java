package com.example.rongimapplication;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.rong.callkit.RongCallKit;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chat(View view) {
        RongCallKit.startSingleCall(this, "2", RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);

//        Conversation.ConversationType conversationType  = Conversation.ConversationType.PRIVATE;
//        String targetId = "1";
//        String title = "dong";
//        String targetId = "2";
//        String title = "Kang";
//
//        RongIM.getInstance().startConversation(MainActivity.this , conversationType, targetId, title);
//        startActivity(new Intent(MainActivity.this, ConversationActivity.class));

//        String content = "消息内容";
//        TextMessage messageContent = TextMessage.obtain(content);
//        Message message = Message.obtain(targetId, conversationType, messageContent);
//        RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
//            /**
//             * 消息发送前回调, 回调时消息已存储数据库
//             * @param message 已存库的消息体
//             */
//            @Override
//            public void onAttached(Message message) {
//
//            }
//            /**
//             * 消息发送成功。
//             * @param message 发送成功后的消息体
//             */
//            @Override
//            public void onSuccess(Message message) {
//                Toast.makeText(MainActivity.this,"onSuccess",Toast.LENGTH_SHORT).show();
//            }
//
//            /**
//             * 消息发送失败
//             * @param message   发送失败的消息体
//             * @param errorCode 具体的错误
//             */
//            @Override
//            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                Toast.makeText(MainActivity.this,"onError"+message.toString(),Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }
}