package com.juai.canvastest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void base(View view) {
        startActivity(new Intent(this,CanvasBaseActivity.class));
    }
    public void plt(View view) {
        startActivity(new Intent(this,PltDrawActivity.class));
    }

}