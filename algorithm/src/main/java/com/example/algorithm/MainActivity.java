package com.example.algorithm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //冒泡排序
    public void bubbleSort(View view) {
        startActivity(new Intent(this,BubbleSortActivity.class));
    }

    //选择排序
    public void selectionSort(View view) {
        startActivity(new Intent(this,SelectionSortActivity.class));
    }

    //插入排序
    public void insertSort(View view) {
        startActivity(new Intent(this,InsertionSortActivity.class));
    }
}