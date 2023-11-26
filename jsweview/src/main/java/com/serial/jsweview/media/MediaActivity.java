package com.serial.jsweview.media;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.serial.jsweview.R;

import java.util.HashMap;
import java.util.Set;

public class MediaActivity extends AppCompatActivity {
    private WebView webview;
    private TextView tvAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        webview = (WebView) findViewById(R.id.webview);
        tvAndroid = (TextView) findViewById(R.id.tv_android);


        initWebView();
    }


    public void initWebView() {
        // 设置与Js交互的权限
        webview.getSettings().setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setSupportMultipleWindows(true);//允许开发多个窗口
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //设置允许JS弹窗


        webview.setWebChromeClient(new WebChromeClient() {
                                       @Override
                                       public void onPermissionRequest(PermissionRequest request) {
                                           Log.e("AAA", "onPermissionRequest: "+request.getResources().length);
                                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                               request.grant(request.getResources());
                                               request.getOrigin();
                                           }
                                       }

                                       @Override
                                       public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                                           result.confirm();
                                           return true;
                                       }

                                       @Override
                                       public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {

                                           String[] acceptTypes = new String[0];
                                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                               acceptTypes = fileChooserParams.getAcceptTypes();
                                           }
                                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                               requestPermissions(new String[]{
                                                       Manifest.permission.CAMERA,
                                                       Manifest.permission.RECORD_AUDIO},1111);
                                           }

                                           Log.e("AAA", "onShowFileChooser: "+acceptTypes[0]);
                                           return true;
                                       }


                                   }
        );

        //步骤1：加载网页
        webview.loadUrl("file:///android_asset/media.html");



    }
}