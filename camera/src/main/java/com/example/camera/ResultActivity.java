package com.example.camera;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lensun.lensuncustomizpro.R;

/**
 * @ClassName ResultActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-10-14 17:25
 * @Version 1.0
 * @UpdateDate 2023-10-14 17:25
 * @UpdateRemark 更新说明
 */
public class ResultActivity extends AppCompatActivity {
    private TextView tvTime;
    private TextView tvResult;


    private String result;
    private int time;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvResult = (TextView) findViewById(R.id.tv_result);

        time=getIntent().getIntExtra("time",time);
        result=getIntent().getStringExtra("result");
        tvTime.setText("解析时间："+time+"毫秒");
        tvResult.setText(result);
    }


}
