package com.xixia.dialogapplication.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import com.xixia.dialogapplication.R;
import com.xixia.dialogapplication.popwindow.TestPopupWindow;

/**
 * @ClassName DialogActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-11-12 13:06
 * @Version 1.0
 * @UpdateDate 2023-11-12 13:06
 * @UpdateRemark 更新说明
 */
public class DialogActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_demo);
    }

    public void dialogclick(View view) {
        CustomProgressDialog customProgressDialog=new CustomProgressDialog(this,R.style.Custom_Progress,"11111");
        customProgressDialog.show();


       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               TestPopupWindow testPopupWindow = new TestPopupWindow(DialogActivity.this);
               testPopupWindow.showAsDropDown(view);
           }
       },1000);
    }

}
