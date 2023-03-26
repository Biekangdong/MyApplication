package com.ruanmeng.lensunc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnAndroidLocation;
    private Button btnGoogleLocation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAndroidLocation = (Button) findViewById(R.id.btn_android_location);
        btnGoogleLocation = (Button) findViewById(R.id.btn_google_location);

        btnAndroidLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(MainActivity.this,AndroidLocationActivity.class));
            }
        });
        btnGoogleLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GoogleLocationActivity.class));
            }
        });
    }


}