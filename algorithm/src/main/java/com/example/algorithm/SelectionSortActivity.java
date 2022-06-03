package com.example.algorithm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Arrays;
import java.util.Random;

/**
 * @ClassName BubbleSortActivity
 * @Description TODO 内容
 * @Author dong
 * @CreateDate 2022/2/14 10:38
 * @Version 1.0
 * @UpdateDate 2022/2/14 10:38
 * @UpdateRemark 更新说明
 */
public class SelectionSortActivity extends Activity {
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvCode;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvCode = (TextView) findViewById(R.id.tv_code);
        tvResult = (TextView) findViewById(R.id.tv_result);

        tvTitle.setText("选择排序");


        tvContent.setText("基本思想：\n" +
                "首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置，然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。以此类推，直到所有元素均排序完毕\n" +
                "\n" +
                "算法描述：\n" +
                "•首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置\n" +
                "•再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾\n" +
                "•重复第二步，直到所有元素均排序完毕\n");

        tvCode.setText(" 代码实现：\n" +
                " public static int[] selectionSort(int[] array) {\n" +
                "        for (int i = 0; i < array.length; i++)\n" +
                "            for (int j = i; j < array.length; j++)\n" +
                "                if (array[j] < array[i]) {\n" +
                "                    int temp = array[j];\n" +
                "                    array[j] = array[i];\n" +
                "                    array[i] = temp;\n" +
                "                }\n" +
                "        return array;\n" +
                "    }");



        int[] data=initData(8); 
        tvResult.setText("结果：\n" +
                "数组："+ Arrays.toString(data)+"\n");
        int[] dataResult=selectionSort(data);
        tvResult.setText(tvResult.getText().toString()+
                         "排序："+Arrays.toString(dataResult)+"\n");


        ImageView imageView = findViewById(R.id.iv_gif);
        String url="file:///android_asset/bubbleSort.gif";
        Glide.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                float totalWidth = ScreenUtils.getScreenWidth(SelectionSortActivity.this)-ScreenUtils.dip2px(SelectionSortActivity.this, 20);
                float totalHeight = totalWidth * 1f / resource.getWidth() * resource.getHeight();
                //背景图片
                LinearLayout.LayoutParams bgLayoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                bgLayoutParams.width = (int) totalWidth;
                bgLayoutParams.height = (int) totalHeight;
                imageView.setLayoutParams(bgLayoutParams);
                Glide.with(SelectionSortActivity.this)
                        .load(url)
                        .into(imageView);
            }
        });
    }

    // 该方法随机生成1-100的随机数
    private static int[] initData(int length) {
        int[] data=new int[length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Random().nextInt(100);
        }
        return data;
    }


    //选择排序
    public static int[] selectionSort(int[] array) {
        for (int i = 0; i < array.length; i++)
            for (int j = i; j < array.length; j++)
                if (array[j] < array[i]) {
                    int temp = array[j];
                    array[j] = array[i];
                    array[i] = temp;
                }
        return array;
    }
}
