package com.xixia.dialogapplication.floatwindows;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class FloatWindowsActivity extends ComponentActivity {
    public WindowManager mWindowManager;
    public View mWindowView;
    public LinearLayout mText;
    public WindowManager.LayoutParams wmParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_float_windows);
    }

    public void folatwindows(View view) {
        checkFloatPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            checkFloatPermission();
        }
    }

    /**
     * 检查是否开启悬浮框权限
     */
    public void checkFloatPermission(){
        if(!Settings.canDrawOverlays(FloatWindowsActivity.this)) {

            Toast.makeText( FloatWindowsActivity.this, "当前无权限，请授权", Toast.LENGTH_SHORT);

            startActivityForResult( new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse( "package:"+ getPackageName())), 0);

        }else {
            initWindowParams();
        }

    }
    /**
     * 初始化Window对象的参数
     */
    private void initWindowParams() {
        //1,获取系统级别的WindowManager
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        //2,添加系统参数，确保悬浮框能显示到手机上
        //电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // flag 设置 Window 属性
        wmParams.flags
                |= WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        //期望的位图格式。默认为不透明
        wmParams.format = PixelFormat.TRANSLUCENT;
        //不许获得焦点
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //窗口停靠位置
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        addWindowViewZWindow();
        initClick();
    }


    /**
     * 添加View到桌面Window界面上
     */
    private void addWindowViewZWindow() {
        if(mWindowView==null){
            mWindowView = LayoutInflater.from(getApplication()).inflate(R.layout.float_layout_window, null);
            mText = (LinearLayout) mWindowView.findViewById(R.id.linear);
        }
        mWindowManager.addView(mWindowView, wmParams);
    }

    /**
     * 点击事件和拖拽事件
     */
    int mStartX, mStartY;
    int mEndX, mEndY;

    private void initClick() {
        mText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //按下鼠标的时候记录下屏幕的位置
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        mEndX = (int) event.getRawX();
                        mEndY = (int) event.getRawY();
                        if (needIntercept()) {
                            //getRawX是触摸位置相对于整个屏幕的位置，getX是控触摸点相对于控件最左边的位置
                            wmParams.x = (int) event.getRawX() - mWindowView.getMeasuredWidth() / 2;
                            wmParams.y = (int) event.getRawY() - mWindowView.getMeasuredHeight() / 2;
                            mWindowManager.updateViewLayout(mWindowView, wmParams);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (needIntercept()) {
                            return true;
                        }
                        break;
                }

                return false;
            }
        });

        mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatWindowsActivity.this, "点击悬浮框", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 判断是否拦截，根据滑动的距离
     *
     * @return
     */
    private boolean needIntercept() {
        if (Math.abs(mStartX - mEndX) > 30 || Math.abs(mStartY - mEndY) > 30) {
            return true;
        }
        return false;
    }
}
