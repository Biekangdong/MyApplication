package com.demo.coordinatorlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerview = (RecyclerView) findViewById(R.id.rcl_view_goods);

        List<String> mList=new ArrayList<>();
        for(int i=0;i<30;i++){
            mList.add("item"+i);
        }
        DataAdapter dataAdapter=new DataAdapter(this,mList);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(dataAdapter);
    }
}