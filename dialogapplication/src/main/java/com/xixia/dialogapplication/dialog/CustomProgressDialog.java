package com.xixia.dialogapplication.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xixia.dialogapplication.R;

/**
 * Created by Administrator on 2017/4/7.
 */
public class CustomProgressDialog extends Dialog {
    private TextView message;
    private ProgressBar imageView;
    private Activity context;
    private String title;
    public CustomProgressDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;

    }
    public CustomProgressDialog(Activity context, int theme,String title) {
        super(context, theme);
        this.context = context;
        this.title=title;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress_custom);
        imageView = (ProgressBar) findViewById(R.id.spinnerImageView);
        message = (TextView) findViewById(R.id.message);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        setTitles(title);
    }

    public void setTitles(String title) {
        if (message != null) {
            message.setText(title);
        }
    }


//    @Override
//    public void show() {
//        if (context == null || context.isDestroyed() || context.isFinishing()) {
//            return;
//        }
//    }
}

