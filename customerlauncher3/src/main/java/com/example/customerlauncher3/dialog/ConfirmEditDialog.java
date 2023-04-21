package com.example.customerlauncher3.dialog;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.customerlauncher3.EditTextClearable;
import com.example.customerlauncher3.R;


/**
 * Created by Administrator on 2017/12/4.
 * 确认dialog
 */

public class ConfirmEditDialog extends Dialog {
    private static final String TAG = "ConfirmEditDialog";

    private Activity mContext;
    private EditTextClearable etContent;
    private TextView tvCancle;
    private TextView tvSure;

    private String content;

    private String cancleString;
    private String sureString;
    private int sureColor;

    public interface DialogViewListener {
        void sureClick(String content);
    }


    private DialogViewListener listener;

    public void setDialogViewListener(DialogViewListener listener) {
        this.listener = listener;
    }


    public ConfirmEditDialog(Activity context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    public ConfirmEditDialog(Activity context, int themeResId, String content) {
        super(context, themeResId);
        mContext = context;
        this.content = content;
    }

    public ConfirmEditDialog(Activity context, int themeResId, String content, String sureString) {
        super(context, themeResId);
        mContext = context;
        this.content = content;
        this.sureString = sureString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_confirm, null);
        setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        //设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.transparent);
        //解决弹出被底部导航栏遮挡问题
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //消除边距
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        //设置Dialog点击外部消失
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.8);
//        lp.width = ScreenUtils.dip2px(mContext,300);
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        lp.y =  ScreenUtils.dip2px(mContext,50);
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        etContent = (EditTextClearable) findViewById(R.id.et_content);
        tvCancle = (TextView) findViewById(R.id.tv_cancle);
        tvSure = (TextView) findViewById(R.id.tv_sure);

        if (!TextUtils.isEmpty(content)) {
            etContent.setText(content);
        }


        if (!TextUtils.isEmpty(cancleString)) {
            tvCancle.setText(cancleString);
        }
        if (!TextUtils.isEmpty(sureString)) {
            tvSure.setText(sureString);
        }

        if (sureColor != 0) {
            tvSure.setTextColor(mContext.getResources().getColor(sureColor));
        }

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etContent.getText().toString())) {
                    return;
                }


                if (listener != null) {
                    listener.sureClick(etContent.getText().toString());
                }

                cancel();
            }
        });
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancel();
            }
        });
    }

    public void setContent(String content) {
        this.content = content;
        etContent.setText(content);
        if (!TextUtils.isEmpty(content)) {
            etContent.setSelection(content.length());//将光标移至文字末尾
        }
    }




    @Override
    public void show() {
        super.show();
        if (etContent.getVisibility() == View.VISIBLE) {
            showSoftInputFromWindow(etContent);
        }

    }

    @Override
    public void dismiss() {
        hideSoftInputFromWindow();
        super.dismiss();
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = mContext.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
