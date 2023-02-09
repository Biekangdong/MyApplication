package io.jayx.jniapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.bob.nativelib.NativeLib;

public class MainActivity extends AppCompatActivity {
    private TextView tvText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = (TextView) findViewById(R.id.tv_text);
        NativeLib nativeLib=new NativeLib();
        tvText.setText(nativeLib.stringFromJNI());
    }
}