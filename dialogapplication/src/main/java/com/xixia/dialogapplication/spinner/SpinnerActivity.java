package com.xixia.dialogapplication.spinner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.xixia.dialogapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SpinnerActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/6/6 17:17
 * @Version 1.0
 * @UpdateDate 2023/6/6 17:17
 * @UpdateRemark 更新说明
 */
public class SpinnerActivity extends Activity {
    private Spinner spinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);
        spinner = (Spinner) findViewById(R.id.spinner);
        //initSystemAdapter();
        initCustomerAdapter();
    }

    /**
     *   系统提供的样式如下
     *    simple_spinner_dropdown_item(列表-间距较高比较好看)
     *    simple_spinner_item(列表-间距紧凑不好看)
     *    simple_list_item_checked（复选框-选中的有绿沟）
     *    simple_list_item_single_choice (单选按钮)
     */
    private void initSystemAdapter(){
        //设置数据源
        List<String> list = new ArrayList<String>();
        list.add("苹果");
        list.add("香蕉");
        list.add("橘子");
        list.add("香蕉");
        //设置系统适配器
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //设置弹出偏移位置
        spinner.setDropDownVerticalOffset(40);
        //点击监听
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SpinnerActivity.this, list.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initCustomerAdapter(){
        //设置数据源
        List<FruitBean> list = new ArrayList<>();
        list.add(new FruitBean("苹果"));
        list.add(new FruitBean("香蕉"));
        list.add(new FruitBean("橘子"));
        list.add(new FruitBean("香蕉"));
        //设置系统适配器
        CustomerAdapter customerAdapter=new CustomerAdapter(this,list);
        spinner.setAdapter(customerAdapter);
        //设置弹出偏移位置
        spinner.setDropDownVerticalOffset(40);
    }
}
