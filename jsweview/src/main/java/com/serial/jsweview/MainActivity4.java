package com.serial.jsweview;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Set;

public class MainActivity4 extends AppCompatActivity {
    private WebView webview;
    private TextView tvAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        webview = (WebView) findViewById(R.id.webview);
        tvAndroid = (TextView) findViewById(R.id.tv_android);


        initWebView();
    }


    public void initWebView() {
        // 设置与Js交互的权限
        webview.getSettings().setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webview.loadUrl("file:///android_asset/index4.html");

        webview.setWebChromeClient(new WebChromeClient() {
                // 拦截输入框(原理同方式2)
                // 参数message:代表promt()的内容（不是url）
                // 参数result:代表输入框的返回值
                @Override
                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                    // 根据协议的参数，判断是否是所需要的url(原理同方式2)
                    // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                    //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                    Uri uri = Uri.parse(message);
                    // 如果url的协议 = 预先约定的 js 协议
                    // 就解析往下解析参数
                    if (uri.getScheme().equals("js")) {

                        // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                        // 所以拦截url,下面JS开始调用Android需要的方法
                        if (uri.getAuthority().equals("webview")) {

                            //
                            // 执行JS所需要调用的逻辑
                            System.out.println("js调用了Android的方法");
                            // 可以在协议上带有参数并传递到Android上
                            HashMap<String, String> params = new HashMap<>();
                            Set<String> collection = uri.getQueryParameterNames();

                            //参数result:代表消息框的返回值(输入值)
                            result.confirm("js调用了Android的方法成功啦");
                        }
                        return true;
                    }
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }


                // 拦截JS的警告框
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }

                // 拦截JS的确认框
                @Override
                public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                    return super.onJsConfirm(view, url, message, result);
                }
            }
        );
    }
}