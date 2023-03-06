package io.jayx.jniapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.bob.nativelib.JNITools;
import com.bob.nativelib.JNITools2;
import com.bob.nativelib.NativeLib;

public class MainActivity extends AppCompatActivity {
    private TextView tvText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = (TextView) findViewById(R.id.tv_text);

        //静态注册
        NativeLib nativeLib=new NativeLib();
        tvText.setText(nativeLib.stringFromJNI());

        //动态注册c库
        JNITools jniTools=new JNITools();
        tvText.setText(String.valueOf(jniTools.add(100,100)));

        //动态注册c++库
        JNITools2 jniTools2=new JNITools2();
        tvText.setText(String.valueOf(jniTools2.add(200,200)));
    }
}