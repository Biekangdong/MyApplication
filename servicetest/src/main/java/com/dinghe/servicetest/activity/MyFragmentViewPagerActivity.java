package com.dinghe.servicetest.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dinghe.servicetest.R;
import com.dinghe.servicetest.fragment.Fragment1;
import com.dinghe.servicetest.fragment.Fragment2;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MyFragmentViewPager
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/17 10:38
 * @Version 1.0
 * @UpdateDate 2023/5/17 10:38
 * @UpdateRemark 更新说明
 */
public class MyFragmentViewPagerActivity extends AppCompatActivity {
    private ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        List<Fragment> viewList = new ArrayList<>();
        viewList.add(new Fragment1());
        viewList.add(new Fragment2());
        MyFragmentPagerAdapter viewPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),viewList);
        //设置ViewPager适配器
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //监听滑动距离
            }

            @Override
            public void onPageSelected(int position) {
                //监听滑动页码
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //监听滑动状态，滑动中还是滑动结束
            }
        });
    }

    /**
     * Fragment适配器
     */
    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        //1.创建Fragment数组
        private List<Fragment> mFragments;

        //2.接收从Activity页面传递过来的Fragment数组
        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments){
            super(fm);
            mFragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}
