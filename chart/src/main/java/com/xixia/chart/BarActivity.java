package com.xixia.chart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarActivity extends AppCompatActivity {
    private BarChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        //获取柱状图控件
        chart = findViewById(R.id.chart1);

        //初始化柱状图控件
        initBarChart();
    }

    /**
     * 初始化柱状图控件
     */
    private void initBarChart(){
        // 是否显示描述
        chart.getDescription().setEnabled(false);

        // 如果图表中显示的条目超过60个，则不会显示任何值
        chart.setMaxVisibleValueCount(60);

        // 只能分别在x轴和y轴上进行缩放
        chart.setPinchZoom(false);

        // 阴影
        chart.setDrawBarShadow(false);
        // 是否绘制背景线
        chart.setDrawGridBackground(false);

        // x坐标绘制
        XAxis xAxis = chart.getXAxis();
        // x坐标位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // x坐标线
        xAxis.setDrawGridLines(false);

        //Y轴左边第1条线
        chart.getAxisLeft().setDrawGridLines(false);


        // 添加一个漂亮平滑的动画
        chart.animateY(1500);

        // 是否绘制图例
        chart.getLegend().setEnabled(false);

        //设置数据
        setData();
    }

    /**
     * 设置数据
     */
    private void setData(){
        //柱状数量 相当于二位数组的 二级数据
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float multi = (10 + 1);
            float val = (float) (Math.random() * multi) + multi / 3;
            values.add(new BarEntry(i, val));
        }

        // 柱状分类，相当于二位数组的 一级数据
        BarDataSet set1 = new BarDataSet(values, "Data Set");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setDrawValues(false);

        // 图表数据集合
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        // 填充图表数据
        BarData data = new BarData(dataSets);
        chart.setData(data);
        chart.setFitBars(true);

        // 刷新图表UI
        chart.invalidate();
    }
}