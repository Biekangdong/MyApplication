package com.xixia.dialogapplication.popwindow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.xixia.dialogapplication.R;

/**
 * @ClassName PopupwindowActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-12-25 14:32
 * @Version 1.0
 * @UpdateDate 2023-12-25 14:32
 * @UpdateRemark 更新说明
 */
public class PopupwindowActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_popupwindows);
    }

    public void popupClick(View view) {
        TestPopupWindow testPopupWindow = new TestPopupWindow(this);
        testPopupWindow.showAsDropDown(view);
    }
}
