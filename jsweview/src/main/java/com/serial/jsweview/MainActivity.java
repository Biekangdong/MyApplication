package com.serial.jsweview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private WebView webView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);

        initWebView();
    }


    public void initWebView() {
        //启用JS脚本
        webView.getSettings().setJavaScriptEnabled(true);
        //支持自动加载图片
        webView.getSettings().setLoadsImagesAutomatically(true);
        //启用内置缩放装置
        webView.getSettings().setSupportZoom(true);  //支持放大缩小
        webView.getSettings().setBuiltInZoomControls(false); //显示缩放按钮
        //设置自适应屏幕，两者合用
        webView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webView.getSettings().setAllowFileAccess(true); // 允许访问文件
        webView.setWebChromeClient(new MyWebChromeClient());

        webView.loadUrl("file:///android_asset/index.html");
        webView.setFocusable(true);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                webView.loadUrl("javascript:say('aaa')");
            }
        });


    }

    /**
     * Created by hp on 2016-2-1.
     */
    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult jsResult) {
            final JsResult finalJsResult;
            finalJsResult = jsResult;
            new AlertDialog.Builder(view.getContext()).setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finalJsResult.confirm();
                }
            }).setCancelable(false).create().show();
            return true;
        }
    }
}