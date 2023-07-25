package com.xixia.chart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

/**
 * @ClassName PieCharActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/25 16:58
 * @Version 1.0
 * @UpdateDate 2023/5/25 16:58
 * @UpdateRemark 更新说明
 */
public class PieCharActivity extends Activity {
    private PieChart chart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        //饼状图控件
        chart = findViewById(R.id.chart1);

        //初始化饼状组件
        initChart();
    }

    private void initChart(){
        //是否用于百分比数据
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        //设置中间文本的字体
        //chart.setCenterTextTypeface(tfLight);
        //chart.setCenterText(generateCenterSpannableText());

        //是否绘制中心圆形区域和颜色
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        //是否绘制中心边透明区域
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        //绘制中中心圆，和圆边的边框大小
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        //是否绘制中心区域文字
        chart.setDrawCenterText(true);

        //默认旋转角度
        chart.setRotationAngle(0);
        //通过触摸启用图表的旋转
        chart.setRotationEnabled(true);
        //触摸进行高亮的突出设置
        chart.setHighlightPerTapEnabled(true);

        //设置单位
        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        //添加选择侦听器
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //选中的扇页
            }

            @Override
            public void onNothingSelected() {
               //未选中的扇页
            }
        });

        //动画
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        //图例
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        //标签样式
        chart.setEntryLabelColor(Color.WHITE);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

        //设置数据
        setData();
    }


    //设置数据
    private void setData() {
        //二维数据的二级数据
        ArrayList<PieEntry> entries = new ArrayList<>();
        //new PieEntry(数值，描述，图标icon)第一个
        entries.add(new PieEntry(40.0f, "数据1", null));
        entries.add(new PieEntry(20.0f, "数据2", null));
        entries.add(new PieEntry(30.0f, "数据3", null));
        entries.add(new PieEntry(10.0f, "数据4", null));

        //二维数据的一级数据
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        //数据配置，是否绘制图标
        dataSet.setDrawIcons(false);
        //扇页之间的空白间距
        dataSet.setSliceSpace(3f);
        //图标偏移
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        //添加颜色集合，
        ArrayList<Integer> colors = new ArrayList<>();
        //colors.add(ColorTemplate.LIBERTY_COLORS[0]);
        colors.add(Color.parseColor("#3790A2"));
        colors.add(Color.parseColor("#37F0A2"));
        colors.add(Color.parseColor("#49DBEE"));
        colors.add(Color.parseColor("#43C088"));
        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        //设置图表数据
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(tfLight);
        chart.setData(data);

        //撤消所有高光
        chart.highlightValues(null);

        //刷新图表UI
        chart.invalidate();
    }
}
