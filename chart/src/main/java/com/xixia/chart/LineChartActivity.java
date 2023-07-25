package com.xixia.chart;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @ClassName LineChartActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/25 17:48
 * @Version 1.0
 * @UpdateDate 2023/5/25 17:48
 * @UpdateRemark 更新说明
 */
public class LineChartActivity extends Activity {
    private LineChart chart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        //折线图表组件
        chart = findViewById(R.id.chart1);

        //初始化图表
        initChart();
    }

    //初始化图表
    private void initChart(){
        //点击监听
        //chart.setOnChartValueSelectedListener(this);
        //绘制网格线
        chart.setDrawGridBackground(false);

        //描述文本
        chart.getDescription().setEnabled(false);

        //是否可以触摸
        chart.setTouchEnabled(true);

        //启用缩放和拖动
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // 如果禁用，可以分别在x轴和y轴上进行缩放
        chart.setPinchZoom(true);

        //设置背景色
        // chart.setBackgroundColor(Color.GRAY);

        //创建自定义MarkerView（扩展MarkerView）并指定布局
        //MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        //mv.setChartView(chart); // For bounds control
        //chart.setMarker(mv); // Set the marker to the chart

        //配置x坐标数据
        XAxis xl = chart.getXAxis();
        xl.setAvoidFirstLastClipping(true);
        xl.setAxisMinimum(0f);

        //配置y坐标左边数据
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setInverted(true);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //关闭y坐标右边数据
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        //抑制最大比例因子
        // chart.setScaleMinima(3f, 3f);

        //将视图居中到图表中的特定位置
        // chart.centerViewPort(10, 50);

        //图例
        Legend l = chart.getLegend();
        //修改图例
        l.setForm(Legend.LegendForm.LINE);

        setData();
    }


    private void setData() {
        //二维数组 一级数据
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float xVal = (i+1);
            float yVal = (float) (Math.random() * 100);
            entries.add(new Entry(xVal, yVal));
        }

        //通过x坐标值排序
        Collections.sort(entries, new EntryXComparator());

        //二维数组 二级数据
        LineDataSet set1 = new LineDataSet(entries, "DataSet 1");

        //折现的宽度合折点的半径大小
        set1.setLineWidth(1.5f);
        set1.setCircleRadius(4f);

        //使用数据集创建数据对象
        LineData data = new LineData(set1);
        chart.setData(data);

        //刷新绘图
        chart.invalidate();
    }

}
