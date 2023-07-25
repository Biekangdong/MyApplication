package com.xixia.dialogapplication.dialogfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.xixia.dialogapplication.R;

/**
 * @ClassName DialogFragmentActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/6/23 0:11
 * @Version 1.0
 * @UpdateDate 2023/6/23 0:11
 * @UpdateRemark 更新说明
 */
public class DialogFragmentActivity extends ComponentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_dialog_fragment);
    }

    public void alertdialog(View view) {
        SystemDialogFragment systemDialogFragment=new SystemDialogFragment();
        systemDialogFragment.show(getFragmentManager(),"System");
    }


    public void customerdialog(View view) {
        CustomerDialogFragment systemDialogFragment=new CustomerDialogFragment();
        systemDialogFragment.show(getFragmentManager(),"Customer");
    }

}
