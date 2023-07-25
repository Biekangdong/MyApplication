package com.dinghe.servicetest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dinghe.servicetest.R;
import com.dinghe.servicetest.fragment.Fragment1;
import com.dinghe.servicetest.fragment.Fragment2;

/**
 * @ClassName TestActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/16 22:17
 * @Version 1.0
 * @UpdateDate 2023/5/16 22:17
 * @UpdateRemark 更新说明
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout flContent;
    private Button button1;
    private Button button2;



    private Fragment1 fragment1;
    private Fragment2 fragment2;

    private  FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                //先隐藏全部Fragment
                hideFragment();
                //添加并显示Fragment1
                fragmentManager = getSupportFragmentManager();
                transaction = fragmentManager.beginTransaction();
                if(fragment1==null){
                    fragment1 = new Fragment1();
                    transaction.add(R.id.fl_content, fragment1);
                }
                transaction.show(fragment1);
                transaction.commit();
                break;
            case R.id.button2:
                //先隐藏全部Fragment
                hideFragment();
                //添加并显示Fragment2
                fragmentManager = getSupportFragmentManager();
                transaction = fragmentManager.beginTransaction();
                if(fragment2==null){
                    fragment2 = new Fragment2();
                    transaction.add(R.id.fl_content, fragment2);
                }
                transaction.show(fragment2);
                transaction.commit();
                break;
        }
    }

    /**
     * 隐藏全部Fragment
     */
    private void hideFragment(){
        if(fragment1!=null){
            transaction.hide(fragment1);
        }

        if(fragment2!=null){
            transaction.hide(fragment2);
        }
        transaction.commit();
    }

    //设置数据
    public void setData(){
        if(fragment1!=null){
            fragment1.setCategoryId("111");
        }
    }

    //获取数据
    public void getData(){
        if(fragment1!=null){
            fragment1.getCategoryId();
        }
    }
}
