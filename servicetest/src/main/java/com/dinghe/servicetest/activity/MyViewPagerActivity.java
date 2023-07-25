package com.dinghe.servicetest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dinghe.servicetest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MyViewPagerActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/17 10:19
 * @Version 1.0
 * @UpdateDate 2023/5/17 10:19
 * @UpdateRemark 更新说明
 */
public class MyViewPagerActivity extends Activity {
    private ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        List<View> viewList = new ArrayList<>();
        viewList.add(new View(this));
        viewList.add(new View(this));
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(viewList);
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
     * ViewPager适配器
     */
    private class ViewPagerAdapter extends PagerAdapter {
        private List<View> viewList;

        public ViewPagerAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        // 获取要滑动的控件的数量，在这里我们以滑动的广告栏为例，那么这里就应该是展示的广告图片的ImageView数量
        @Override
        public int getCount() {
            return viewList.size();
        }

        // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(viewList.get(position));
        }

        // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(viewList.get(position));
            return viewList.get(position);
        }
    }

}
