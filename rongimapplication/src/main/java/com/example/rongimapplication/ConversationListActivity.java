package com.example.rongimapplication;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.rong.imkit.conversationlist.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * @ClassName ConversationListActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/3/26 14:35
 * @Version 1.0
 * @UpdateDate 2023/3/26 14:35
 * @UpdateRemark 更新说明
 */
public class ConversationListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        ConversationListFragment conversationListFragment=new ConversationListFragment();
        // 此处设置 Uri. 通过 appendQueryParameter 去设置所要支持的会话类型. 例如
        // .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(),"false")
        // 表示支持单聊会话, false 表示不聚合显示, true 则为聚合显示
//        Uri uri = Uri.parse("rong://" +
//                        getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversationlist")
//                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
//                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
//                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
//                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
//                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
//                .build();

//        conversationListFragment.setUri(uri);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, conversationListFragment);
        transaction.commit();

    }
}
