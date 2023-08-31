package com.juai.usbmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

public class ScanKeyActivity extends AppCompatActivity {
    private static final String TAG = "ScanKeyActivity";
    private LinearLayout viewRoot;
    private EditText etKey;
    private TextView tvTxt;


    private ScannerGunManager scannerGunManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_key);


        viewRoot = (LinearLayout) findViewById(R.id.view_root);
        etKey = (EditText) findViewById(R.id.et_key);
        tvTxt = (TextView) findViewById(R.id.tv_txt);


        onKeyBoardListener();
        scannerGunManager =   new ScannerGunManager(new ScannerGunManager.OnScanListener() {
            @Override
            public void onResult(String code) {
                Log.d(TAG, "code= " + code);
                tvTxt.setText(code);
            }
        });

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "event= " + event);
        if(scannerGunManager.dispatchKeyEvent(event)){
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }

    //监听软件盘是否弹起
    private void onKeyBoardListener() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                Log.e("软键盘", "键盘显示 高度" + height);
                scannerGunManager.setInterrupt(false);

                etKey.setFocusable(true);
                etKey.setFocusableInTouchMode(true);
            }

            @Override
            public void keyBoardHide(int height) {
                Log.e("软键盘", "键盘隐藏 高度" + height);
                scannerGunManager.setInterrupt(true);

                etKey.setFocusable(false);
                etKey.setFocusableInTouchMode(true);

            }
        });
    }

}