package com.bob.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView tvText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = (TextView) findViewById(R.id.tv_text);

        SntpClient client = new SntpClient();
        if (client.requestTime("pool.ntp.org", 30000)) {
            long now = client.getNtpTime() + System.nanoTime() / 1000
                    - client.getNtpTimeReference();
            Date current = new Date(now);
            tvText.setText(current.toString());
        }
    }
}