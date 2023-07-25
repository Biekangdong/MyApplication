package com.dinghe.schemetest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

/**
 * @ClassName ActionActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/12 14:24
 * @Version 1.0
 * @UpdateDate 2023/5/12 14:24
 * @UpdateRemark 更新说明
 */
public class ActionActivity extends AppCompatActivity {
    private TextView tvContent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);


        tvContent = (TextView) findViewById(R.id.tv_content);

        Intent intent = getIntent();
        Uri data = intent.getData();  //
        String action = intent.getAction();
        String scheme = intent.getScheme();
        Set<String> categories = intent.getCategories();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("data：").append(data).append("\n\n")
                .append("action：").append(action).append("\n\n")
                .append("categories：").append(categories).append("\n\n")
        ;


        tvContent.setText(stringBuilder);
    }
}
