package com.serial.jsweview.jsandroid;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.serial.jsweview.R;

public class MainActivity2 extends AppCompatActivity {
    private WebView webview;
    private TextView tvAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        webview = (WebView) findViewById(R.id.webview);
        tvAndroid = (TextView) findViewById(R.id.tv_android);
        tvAndroid.setText("//继承自Object类，别名是aa,即在html可以直接用aa.showToast(\"哈哈哈\")来调用android方法\n" +
                "public class MyObject extends Object {\n" +
                "    @JavascriptInterface\n" +
                "    public void showToast(String name){\n" +
                "         Toast.makeText(MainActivity2.this, \"您好!\"+name, Toast.LENGTH_SHORT).show();\n" +
                "    }\n" +
                "}");
        
        initWebView();
    }


    public void initWebView() {
        // 设置与Js交互的权限
        webview.getSettings().setJavaScriptEnabled(true);

        //将java对象暴露给JavaScript脚本
        //参数1：java对象，里面定义了java方法
        //参数2：Java对象在js里的对象名，可以看作第一个参数的别名，可以随便取，即在html可以直接用aa.showToast("哈哈哈")来调用android方法
        webview.addJavascriptInterface(new MyObject(), "aa");//AndroidtoJS类对象映射到js的test对象

        //加载网页
        webview.loadUrl("file:///android_asset/index2.html");
    }

    //继承自Object类，别名是aa,即在html可以直接用aa.showToast("哈哈哈")来调用android方法
    public class MyObject extends Object {
        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void showToast(String name){
            Toast.makeText(MainActivity2.this, "您好!"+name, Toast.LENGTH_SHORT).show();
        }
    }
}