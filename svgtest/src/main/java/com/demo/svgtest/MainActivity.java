package com.demo.svgtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * SVG PATH（X轴为横向向右的坐标轴，Y轴为竖向向下的坐标轴，（0，0）为中心点）M M = moveto(X,Y) ：将画笔移动到（X,Y）坐标位置
 * L L = lineto(X,Y) ：画直线到（X,Y）坐标位置
 * H H = horizontal lineto(X)：画水平线到指定的X坐标位置
 * V V = vertical lineto(Y)：画垂直线到指定的Y坐标位置
 * C C = curveto(X1,Y1,X2,Y2,ENDX,ENDY)：三阶贝赛尔曲线
 * S S = smooth curveto(X2,Y2,ENDX,ENDY)
 * Q Q = quadratic Belzier curve(X,Y,ENDX,ENDY)：二阶贝赛尔曲线
 * T T = smooth quadratic Belzier curveto(ENDX,ENDY)：映射
 * A A = elliptical Arc(RX,RY,XROTATION,FLAG1,FLAG2,X,Y)：弧线
 * Z Z = closepath()：关闭路径
 *
 * M0,30 L20,0 L140,0 L160,30 L140,60 L20,60 L0,30Z  
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}