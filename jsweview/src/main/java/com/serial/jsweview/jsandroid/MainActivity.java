package com.serial.jsweview.jsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.serial.jsweview.R;

public class MainActivity extends AppCompatActivity {
    private WebView webview;
    private TextView tvAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webview = (WebView) findViewById(R.id.webview);
        tvAndroid = (TextView) findViewById(R.id.tv_android);
        tvAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Android调用js方法
                //Android 4.4以下使用loadUrl，Android 4.4以上evaluateJavascript
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    webview.loadUrl("javascript:callJS('aaa')");
                } else {
                    webview.evaluateJavascript("javascript:callJS('aaa')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                            Toast.makeText(MainActivity.this,value,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        
        initWebView();
    }


    public void initWebView() {
        //启用JS脚本
        webview.getSettings().setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //加载网页
        webview.loadUrl("file:///android_asset/index.html");

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult jsResult) {
                new AlertDialog.Builder(view.getContext()).setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsResult.confirm();
                    }
                }).setCancelable(false).create().show();
                return true;
            }
        });

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient() {
            //override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                handler.proceed("admin", "sunlight");
                int d = Log.d("MyWebViewClient", "onReceivedHttpAuthRequest");
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String uri) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(uri);
                return true;
            }
        });
    }
}