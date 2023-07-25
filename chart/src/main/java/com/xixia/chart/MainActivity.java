package com.xixia.chart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void chart1(View view) {
        startActivity(new Intent(this,BarActivity.class));
    }

    public void chart2(View view) {
        startActivity(new Intent(this,PieCharActivity.class));
    }

    public void chart3(View view) {
        startActivity(new Intent(this,LineChartActivity.class));
    }

}