package com.dinghe.schemetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toScheme(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("goods://test:8080/details?id=222"));
        startActivity(intent);
    }

    public void toWebview(View view) {
        Intent intent = new Intent(this,WebviewActivity.class);
        startActivity(intent);
    }

    public void toAction(View view) {
        Intent intent = new Intent();
        intent.setAction(getPackageName()+".customerAction");
        startActivity(intent);
    }
}