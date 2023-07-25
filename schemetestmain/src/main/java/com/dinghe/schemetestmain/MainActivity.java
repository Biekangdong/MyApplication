package com.dinghe.schemetestmain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void toActivity1(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = new ComponentName("com.dinghe.schemetest", "com.dinghe.schemetest.MainActivity");
        intent.setComponent(componentName);
        startActivity(intent);
    }

    public void toActivity2(View view) {
        Intent intent = new Intent(this,WebviewActivity.class);
        startActivity(intent);
    }

    public void toActivity3(View view) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = new ComponentName("com.dinghe.schemetest", "com.dinghe.schemetest.ActionActivity");
        intent.setComponent(componentName);
        startActivity(intent);
    }
}