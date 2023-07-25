package com.dinghe.schemetestmain;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class WebviewActivity extends AppCompatActivity {
    private WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        
        webview = (WebView) findViewById(R.id.webview);

        initWebView();
    }


    public void initWebView() {
        //加载网页
        webview.loadUrl("file:///android_asset/test.html");
    }
}