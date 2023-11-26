package com.juai.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * @ClassName PreviewSurfaceView
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-09-19 9:22
 * @Version 1.0
 * @UpdateDate 2023-09-19 9:22
 * @UpdateRemark 更新说明
 */
public class PreviewSurfaceView extends SurfaceView {
    private Context mContext;
    private Paint paint;// 画布
    private SurfaceHolder surfaceHolder; // 用于控制SurfaceView
    private Canvas canvas;// 画布
    private boolean flag;// 关闭线程标志
    private Thread thread;// 新建线程
    private Bitmap bitmap;// 位图

    private int touchX, touchY;//点击的像素位置

    private Bitmap selector;//焦点位置
    private int margin;//距离中间位置的间距，构成预览区域

    public PreviewSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        mContext = context;

        // 实例SurfaceHolder
        surfaceHolder = this.getHolder();
        // 为SurfaceView添加状态监听
        surfaceHolder.addCallback(callback);
        // 实例一个画笔
        paint = new Paint();
        // 设置画笔颜色为白色
        paint.setColor(Color.WHITE);
        // 设置焦点
        setFocusable(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test2);

        margin = dip2px(50);

        //添加锚点
        selector = BitmapFactory.decodeResource(getResources(), R.mipmap.location);
    }

    //页面状态回调监听
    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            flag = true;
            // 实例线程
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (flag) {
                        draw();
//                        try {
//                            Thread.sleep(50);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
                    }
                }
            });
            // 启动线程
            thread.start();
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            flag = false;//结束游设置线程关闭标志为false
        }
    };


    public void setTouchXY(int mTouchX, int mTouchY) {
        flag = true;
        touchX = mTouchX;
        touchY = mTouchY;
    }

    /**
     * 绘制
     */
    public void draw() {
        synchronized (this) {
            try {
                // 虽然Surface保存了当前窗口的像素数据，但是在使用过程中是不直接和Surface打交道的，
                // 由SurfaceHolder的Canvas lockCanvas()或则Canvas lockCanvas(Rect dirty)函数来获取Canvas对象，
                // 通过在Canvas上绘制内容来修改Surface中的数据
                // 锁定画布
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    //旋转位图(方式1)
                    canvas.save();
                    int left = Math.max(touchX - margin, 0);
                    int top = Math.max(touchY - margin, 0);
                    if (left + margin * 2 > bitmap.getWidth()) {
                        left = bitmap.getWidth() - margin * 2;
                    }
                    if (top + margin * 2 > bitmap.getHeight()) {
                        top = bitmap.getHeight() - margin * 2;
                    }
                    int right = Math.min(left + margin * 2, bitmap.getWidth());
                    int bottom = Math.min(top + margin * 2, bitmap.getHeight());

                    Rect srcRect = new Rect(left, top, right, bottom);//截取图片
                    Rect dstRect = new Rect(0, 0, right - left, bottom - top);//绘制的矩形区域
                    canvas.drawBitmap(bitmap, srcRect, dstRect, paint);


                    //绘制锚点
                    int selectorWidth = dip2px(10);

                    int previewWidth = right - left;
                    int previewHeight = bottom - top;
                    int selectorLeft = (int) (previewWidth / 2f - selectorWidth / 2f);
                    int selectorTop = (int) (previewHeight / 2f - selectorWidth / 2f);
                    if (touchX < margin) {
                        selectorLeft = touchX - selectorWidth / 2;
                    }
                    if (touchY < margin) {
                        selectorTop = touchY - selectorWidth / 2;
                    }

                    if (touchX > bitmap.getWidth() - margin) {
                        float marginRight = bitmap.getWidth() - (touchX - selectorWidth / 2f);
                        selectorLeft = (int) (margin * 2 - marginRight);
                    }
                    if (touchY > bitmap.getHeight() - margin) {
                        float marginBottom = bitmap.getHeight() - (touchY - selectorWidth / 2f);
                        selectorTop = (int) (margin * 2 - marginBottom);
                    }

                    Rect selectorDstRect = new Rect(selectorLeft, selectorTop, selectorLeft + selectorWidth, selectorTop + selectorWidth);
                    canvas.drawBitmap(selector, null, selectorDstRect, paint);

                    canvas.restore();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //绘制完后再解锁画布
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void stop() {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
