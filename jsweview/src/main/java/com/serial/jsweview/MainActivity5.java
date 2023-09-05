package com.serial.jsweview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Set;

/**
 * @ClassName MainActivity5
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/8/31 16:50
 * @Version 1.0
 * @UpdateDate 2023/8/31 16:50
 * @UpdateRemark 更新说明
 */
public class MainActivity5 extends AppCompatActivity {
    private WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        webview = (WebView) findViewById(R.id.webview);



        initWebView();
    }


    public void initWebView() {
        // 设置与Js交互的权限
        webview.getSettings().setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        //步骤1：加载网页
        webview.addJavascriptInterface(new MJavascriptInterface(MainActivity5.this), "imagelistener");
        // 复写WebViewClient类的shouldOverrideUrlLoading方法
        webview.setWebViewClient(new MyWebViewClient());

        webview.loadUrl("https://cms-pre.juaiyouxuan.com/topicPage/contentMsgEdit?subjectId=782756536078086144&subjectName=%E6%B8%90%E5%8F%98%E8%89%B2%E8%A7%92%E5%BA%A6%E6%B5%8B%E8%AF%95&customerId=601817");

    }
    //自定义Javascript 接口，实现点击图片放大
    public class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            addImageClickListener(view);//待网页加载完全后设置图片点击的监听方法
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        private void addImageClickListener(WebView webView) {
//            webView.loadUrl("javascript:(function(){" +
//                    "var objs = document.getElementsByTagName(\"img\"); " +
//                    "for(var i=0;i<objs.length;i++)  " +
//                    "{"
//                    + "    objs[i].onclick=function()  " +
//                    "    {  "
//                    + "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
//                    "    }  " +
//                    "}" +
//                    "})()");
            webView.loadUrl("javascript:(function(){" +
                    "var paramDiv=document.getElementById(\"div\");\n" +
                    "var childs=paramDiv.childNodes;\n" +
                    "for(var i=0,n=childs.length;i<n;i++){\n" +
                    "    var component=childs[i];\n" +
                    "    if(component.type=='img'){\n" +
                    "        window.imagelistener.openImage(this.src);  "+
                    "    }\n" +
                    "}" +
                    "})()");

        }
    }


    public class MJavascriptInterface {
        private Context context;

        public MJavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            Log.e("EEE", "openImage: "+img );
            Message message=new Message();
            message.what=1;
            message.obj=img;
            handler.sendMessage(message);
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                Toast.makeText(MainActivity5.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();
                return true;
            }


            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
