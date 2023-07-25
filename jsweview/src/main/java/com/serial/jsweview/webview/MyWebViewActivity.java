package com.serial.jsweview.webview;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.serial.jsweview.R;

/**
 * @ClassName HuofangyongrouActivity
 * @Description TODO 原生Webview
 * @Author biekangdong
 * @CreateDate 2023/3/6 11:04
 * @Version 1.0
 * @UpdateDate 2023/3/6 11:04
 * @UpdateRemark 更新说明
 */
public class MyWebViewActivity extends Activity {
    private FrameLayout flWeb;
    private LinearLayout llView;
    private ProgressBar progressBar;


    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_webview);

        initView();
    }


    protected void initView() {
        flWeb = findViewById(R.id.fl_web);
        llView = (LinearLayout) findViewById(R.id.ll_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        initWebView("https://www.baidu.com");
    }




    public void initWebView(String url) {
        webView=new WebView(this);
        flWeb.removeAllViews();
        flWeb.addView(webView);
        // 设置支持javascript
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);
        //是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);
        //是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);
        // 是否显示缩放按钮，默认false
        webView.getSettings().setUseWideViewPort(true);
        // 设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);
        // 和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setAppCacheEnabled(true);
        // 是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
        //访问网页
        webView.loadUrl(url);

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String uri) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(uri);
                return true;
            }
        });

        //WebView 事件回调监听
        llView.setVisibility(View.VISIBLE);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress >=80) {
                   llView.setVisibility(View.GONE);
                } else {
                  llView.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView!=null&&webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();

    }

}
