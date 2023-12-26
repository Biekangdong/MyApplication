package com.xixia.dialogapplication.toast;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hjq.toast.ToastUtils;
import com.xixia.dialogapplication.R;

/**
 * @ClassName ToastActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-12-16 9:52
 * @Version 1.0
 * @UpdateDate 2023-12-16 9:52
 * @UpdateRemark 更新说明
 */
public class ToastActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
    }


    public void systemToast(View view) {
        Toast.makeText(this,"系统吐司",Toast.LENGTH_SHORT).show();
    }

    public void customerToast(View view) {
    }

    public void customerToast2(View view) {
        ToastUtils.show("自定义三方吐司");
    }
}
