package com.xixia.dialogapplication.snackbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.xixia.dialogapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SnackBarActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/6/22 19:52
 * @Version 1.0
 * @UpdateDate 2023/6/22 19:52
 * @UpdateRemark 更新说明
 */
public class SnackBarActivity extends Activity {
    private LinearLayout llRootLayout;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_bar);

        llRootLayout = (LinearLayout) findViewById(R.id.ll_root_layout);
    }

    public void snackbar1(View view) {
        Snackbar.make(this,llRootLayout,"snack bar",Snackbar.LENGTH_SHORT).show();
    }

    public void snackbar2(View view) {
        Snackbar snack_bar = Snackbar.make(this,view, "确定退出吗？", Snackbar.LENGTH_INDEFINITE);
        snack_bar.setAction("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出
            }
        });
        snack_bar.show();
    }

    public void snackbar3(View view) {
        SnackBarUtil.show(this, view, "确定退出吗？", "确认", new SnackBarUtil.SnackBarOnClick() {
            @Override
            public void clickEvent(Snackbar snackbar) {
                //退出
            }
        });
    }


}
