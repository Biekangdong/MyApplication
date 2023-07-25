package com.dinghe.schemetest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

/**
 * @ClassName SchemeActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/12 11:04
 * @Version 1.0
 * @UpdateDate 2023/5/12 11:04
 * @UpdateRemark 更新说明
 */
public class SchemeActivity extends AppCompatActivity {
    private TextView tvContent;

    String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);

        id=getIntent().getStringExtra("id");



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
                .append("scheme：").append(scheme).append("\n\n")
                .append("id：").append(data.getQueryParameterNames()).append("\n\n")
                .append("host：").append(data.getHost()).append("\n\n")
                .append("path：").append(data.getPath()).append("\n\n")
                .append("port：").append(data.getPort()).append("\n\n")
                ;


        tvContent.setText(stringBuilder);
    }
}
