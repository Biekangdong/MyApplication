package com.juai.canvastest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class CanvasBaseView extends View {
    private static final String TAG = "CanvasBaseView";
    //默认属性
    private int paintColor;
    private int defaultWidth;
    private int defaultHeight;

    public CanvasBaseView(Context context) {
        super(context);
    }

    public CanvasBaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //默认属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CavasBaseView);
        paintColor = array.getColor(R.styleable.CavasBaseView_paint_color, Color.RED);
        defaultWidth = array.getColor(R.styleable.CavasBaseView_default_width, 0);
        defaultHeight = array.getColor(R.styleable.CavasBaseView_default_height,0);
        array.recycle();
    }


    //    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 自己算的画 没有必要再让 view 自己测量一遍了，浪费资源
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        Log.d(TAG, "宽的模式:"+widthMode);
        Log.d(TAG, "宽的尺寸:"+widthSize);
        Log.d(TAG, "高的模式:"+heightMode);
        Log.d(TAG, "高的尺寸:"+heightSize);

        // 开始计算宽度
        int widthResult = 0;
        switch (widthMode) {
            case MeasureSpec.AT_MOST://不超过
                // 在 AT_MOST 模式下，取二者的最小值
                //widthSize测量的实际宽高，widthResult期望的最大宽高
                widthResult=Math.min(widthSize,defaultWidth);
                break;
            case MeasureSpec.EXACTLY://精准的
                // 父 View 给多少用多少
                widthResult = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED://无限大，没有指定大小
                // 默认的大小
                widthResult = defaultWidth;
                break;
            default:
                widthResult = 0;
                break;
        }



        // 开始计算宽度
        int heightResult = 0;
        switch (widthMode) {
            case MeasureSpec.AT_MOST://不超过
                // 在 AT_MOST 模式下，取二者的最小值
                // heightSize测量的实际宽高，heightResult期望的最大宽高
                heightResult=Math.min(heightSize,heightResult);
                break;
            case MeasureSpec.EXACTLY://精准的
                // 父 View 给多少用多少
                heightResult = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED://无限大，没有指定大小
                // 使用计算出的大小
                heightResult = heightResult;
                break;
            default:
                heightResult = 0;
                break;
        }
        // 设置最终的宽高
        setMeasuredDimension(widthResult, heightResult);
    }

//   //上面模版代码其实 Android SDK 里面早就有了很好的封装 ： resolveSize(int size, int measureSpec) 和 resolveSizeAndState(int size, int measureSpec, int childMeasuredState) ，两行代码直接搞定。
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        // 没有必要再让 view 自己测量一遍了，浪费资源
//        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        // 指定期望的 size
//        int width = resolveSize(defaultWidth, widthMeasureSpec);
//        int height = resolveSize(defaultHeight, heightMeasureSpec);
//        // 设置大小
//        setMeasuredDimension(width, height);
//    }

    int scale=3;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 创建画笔
        Paint p = new Paint();
        p.setColor(Color.RED);// 设置红色
        p.setTextSize(16);

        canvas.drawText("画圆：", 10*scale, 20*scale, p);// 画文本
        canvas.drawCircle(60*scale, 20*scale, 10*scale, p);// 小圆
        p.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除
        canvas.drawCircle(120*scale, 20*scale, 20*scale, p);// 大圆

        canvas.drawText("画线及弧线：", 10*scale, 60*scale, p);
        p.setColor(Color.GREEN);// 设置绿色
        canvas.drawLine(60*scale, 40*scale, 100*scale, 40*scale, p);// 画线
        canvas.drawLine(110*scale, 40*scale, 190*scale, 80*scale, p);// 斜线
        //画笑脸弧线
        p.setStyle(Paint.Style.STROKE);//设置空心
        RectF oval1=new RectF(150*scale,20*scale,180*scale,40*scale);
        canvas.drawArc(oval1, 180, 180, false, p);//小弧形
        oval1.set(190*scale, 20*scale, 220*scale, 40*scale);
        canvas.drawArc(oval1, 180, 180, false, p);//小弧形
        oval1.set(160*scale, 30*scale, 210*scale, 60*scale);
        canvas.drawArc(oval1, 0, 180, false, p);//小弧形

        canvas.drawText("画矩形：", 10*scale, 80*scale, p);
        p.setColor(Color.GRAY);// 设置灰色
        p.setStyle(Paint.Style.FILL);//设置填满
        canvas.drawRect(60*scale, 60*scale, 80*scale, 80*scale, p);// 正方形
        canvas.drawRect(60*scale, 90*scale, 160*scale, 100*scale, p);// 长方形

        canvas.drawText("画扇形和椭圆:", 10*scale, 120*scale, p);
        Shader mShader = new LinearGradient(0*scale, 0*scale, 100*scale, 100*scale,
                new int[] { Color.RED, Color.GREEN,Color.YELLOW}, null, Shader.TileMode.REPEAT); // 一个材质,打造出一个线性梯度沿著一条线。
        p.setShader(mShader);
        // p.setColor(Color.BLUE);
        RectF oval2 = new RectF(60*scale, 100*scale, 200*scale, 240*scale);// 设置个新的长方形，扫描测量
        canvas.drawArc(oval2, 200, 130, true, p);
        // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，第四个参数是真的时候画扇形，是假的时候画弧线
        //画椭圆，把oval改一下
        oval2.set(210*scale,100*scale,250*scale,130*scale);
        canvas.drawOval(oval2, p);

        canvas.drawText("画三角形：", 10*scale, 200*scale, p);
        // 绘制这个三角形,你可以绘制任意多边形
        Path path = new Path();
        path.moveTo(80*scale, 200*scale);// 此点为多边形的起点
        path.lineTo(120*scale, 250*scale);
        path.lineTo(80*scale, 250*scale);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 你可以绘制很多任意多边形，比如下面画六连形
        p.reset();//重置
        p.setColor(Color.LTGRAY);
        p.setStyle(Paint.Style.STROKE);//设置空心
        Path path1=new Path();
        path1.moveTo(180*scale, 200*scale);
        path1.lineTo(200*scale, 200*scale);
        path1.lineTo(210*scale, 210*scale);
        path1.lineTo(200*scale, 220*scale);
        path1.lineTo(180*scale, 220*scale);
        path1.lineTo(170*scale, 210*scale);
        path1.close();//封闭
        canvas.drawPath(path1, p);


        //画圆角矩形
        p.setStyle(Paint.Style.FILL);//充满
        p.setColor(Color.LTGRAY);
        p.setAntiAlias(true);// 设置画笔的锯齿效果
        canvas.drawText("画圆角矩形:", 10*scale, 260*scale, p);
        RectF oval3 = new RectF(80*scale, 260*scale, 200*scale, 300*scale);// 设置个新的长方形
        canvas.drawRoundRect(oval3, 20*scale, 15*scale, p);//第二个参数是x半径，第三个参数是y半径

        //画贝塞尔曲线
        canvas.drawText("画贝塞尔曲线:", 10*scale, 310*scale, p);
        p.reset();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.GREEN);
        Path path2=new Path();
        path2.moveTo(100*scale, 320*scale);//设置Path的起点
        path2.quadTo(150*scale, 310*scale, 170*scale, 400*scale); //设置贝塞尔曲线的控制点坐标和终点坐标
        canvas.drawPath(path2, p);//画出贝塞尔曲线

        //画点
        p.setStyle(Paint.Style.FILL);
        canvas.drawText("画点：", 10*scale, 390*scale, p);
        canvas.drawPoint(60*scale, 390*scale, p);//画一个点
        canvas.drawPoints(new float[]{60*scale,400*scale,65*scale,400*scale,70*scale,400*scale}, p);//画多个点

        //画图片，就是贴图
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        canvas.drawBitmap(bitmap, 250,360, p);
    }
}
