package com.xixia.chart;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName LineChartViewUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/25 21:28
 * @Version 1.0
 * @UpdateDate 2023/5/25 21:28
 * @UpdateRemark 更新说明
 */
public class LineChartViewUtils {
    private Context context;
    public LineChartViewUtils(Context context) {
        this.context=context;
    }

    //配置折线数据
    private void initChart(LineChart chart) {
        //关闭描述
        chart.getDescription().setEnabled(false);
        //关闭高亮
        chart.setHighlightPerDragEnabled(false);

        //关闭图例
        Legend l = chart.getLegend();
        l.setEnabled(false);

        //x轴数据
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.parseColor("#DADADA"));
        xAxis.setAxisLineWidth(0.5f);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#DADADA"));
        xAxis.setGridLineWidth(0.5f);
        chart.getXAxis().setValueFormatter(indexAxisValueFormatter);

        //Y轴数据
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(Color.TRANSPARENT);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        //最小Y轴
        //leftAxis.setAxisMinimum(0f);
        //最大Y轴
        //leftAxis.setAxisMaximum(170f);
        //Y轴偏移
        //leftAxis.setYOffset(-9f);

        //关闭右边Y轴数值显示
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        rightAxis.setTextColor(Color.TRANSPARENT);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
    }

    int position = 0;
    //时间格式化
    SimpleDateFormat mFormatHour = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    SimpleDateFormat mFormatMonth = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
    SimpleDateFormat mFormatYear = new SimpleDateFormat("MM", Locale.ENGLISH);
    IndexAxisValueFormatter indexAxisValueFormatter = new IndexAxisValueFormatter() {
        @Override
        public String getFormattedValue(float value) {
            String valueString = "";
            long millis = System.currentTimeMillis();
            switch (position) {
                case 0:
                case 1:
                    valueString = mFormatHour.format(new Date(millis));
                    break;
                case 2:
                    valueString = getWeekOfDate(millis);
                    break;
                case 3:
                    valueString = mFormatMonth.format(new Date(millis));
                    break;
                case 4:
                    valueString = mFormatYear.format(new Date(millis));
                    break;
            }
            return valueString;
        }
    };

    //根据时间戳获取星期
    public static String getWeekOfDate(long timestamp) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timestamp));
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    //设置数据
    public void setCharDataList(LineChart chart, ArrayList<Entry> entryList, List<Float> valueList, float allValue, String unit) {
        //平均值
        float average = allValue / 10;
        LimitLine limitLine = new LimitLine(average, average + unit + " 平均");
        limitLine.enableDashedLine(10f, 10f, 0f);
        if (Collections.max(valueList) - average > 0.3) {
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        } else {
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        }
        limitLine.setTextSize(10f);
        limitLine.setLineColor(Color.parseColor("#6b9cdf"));
        limitLine.setLineWidth(0.5f);
        limitLine.setTextSize(10);
        limitLine.setTextColor(Color.parseColor("#6b9cdf"));
        limitLine.setYOffset(10);

        //Y轴左右平均值数据
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.removeAllLimitLines();
        rightAxis.removeAllLimitLines();
        leftAxis.setDrawLimitLinesBehindData(true);
        rightAxis.setDrawLimitLinesBehindData(true);
        //Y轴左右添加平均线
        leftAxis.addLimitLine(limitLine);
        rightAxis.addLimitLine(limitLine);

        //填充数据
        LineDataSet set1 = new LineDataSet(entryList, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.parseColor("#6b9cdf"));
        set1.setHighLightColor(Color.parseColor("#6b9cdf"));
        set1.setDrawCircleHole(false);

        //使用数据集创建数据对象
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        //这是折线数据
        chart.setData(data);
        //动画
        //chart.animateX(1000);
        //chart.animateY(1000);
        //更新图表UI
        chart.invalidate();
    }


    //点击事件
    private void onViewClick(){
        setData(0);
    }
    //设置温度，湿度，华氏度图表数据
    public void setData(int tabSelectPosition) {
        //温度℃
        ArrayList<Entry> valuesTemperature = new ArrayList<>();//温度图表数据
        float allValueTemperature = 0;//温度平均值
        List<Float> temperatureList = new ArrayList<>();//温度集合
        //温度F
        ArrayList<Entry> valuesTemperatureFF = new ArrayList<>();//温度图表数据
        float allValueTemperatureFF = 0;//温度平均值
        List<Float> temperatureListFF = new ArrayList<>();//温度集合
        //湿度
        ArrayList<Entry> valuesHumidity = new ArrayList<>();//湿度图表数据
        float allValueHumidity = 0;//湿度平均值
        List<Float> humidityList = new ArrayList<>();//湿度集合

        //温度合集
        List<String> list = new ArrayList<>();
        list.add("50");
        list.add("20");
        list.add("40");
        list.add("60");
        list.add("40");
        list.add("30");
        for (int i = 0; i < list.size(); i++) {
            String temperature = list.get(i);
            String humidity = String.valueOf(Math.random());
            //温度℃
            temperatureList.add(Float.valueOf(temperature));
            allValueTemperature += Float.parseFloat(temperature);
            valuesTemperature.add(new Entry(i, Float.parseFloat(temperature)));
            //温度℉
            temperatureListFF.add(Float.valueOf(temperature) * 1.8f + 32);
            allValueTemperatureFF += Float.parseFloat(temperature) * 1.8f + 32;
            valuesTemperatureFF.add(new Entry(i, Float.parseFloat(temperature) * 1.8f + 32));
            //湿度
            humidityList.add(Float.valueOf(humidity));
            allValueHumidity += Float.parseFloat(humidity);
            valuesHumidity.add(new Entry(i, Float.parseFloat(humidity)));
        }

        int positon = 0;
        switch (positon) {
            case 0:
                setCharDataList(new LineChart(context), valuesTemperatureFF, temperatureListFF, allValueTemperatureFF, "℉");
                break;
            case 1:
                setCharDataList(new LineChart(context), valuesTemperature, temperatureList, allValueTemperature, "℃");
                break;
            case 2:
                setCharDataList(new LineChart(context), valuesHumidity, humidityList, allValueHumidity, "%");
                break;
        }
    }
}
