package com.juai.canvastest;

import android.app.Activity;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * @ClassName CanvasBaseActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/9/5 17:13
 * @Version 1.0
 * @UpdateDate 2023/9/5 17:13
 * @UpdateRemark 更新说明
 */
public class CanvasBaseActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_base);

    }
}
