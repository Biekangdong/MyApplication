package com.example.rongimapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.rong.imkit.conversation.ConversationFragment;

/**
 * @ClassName ConversationActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/3/26 14:32
 * @Version 1.0
 * @UpdateDate 2023/3/26 14:32
 * @UpdateRemark 更新说明
 */
public class ConversationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // 添加会话界面
        ConversationFragment conversationFragment = new ConversationFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, conversationFragment);
        transaction.commit();
    }
}
