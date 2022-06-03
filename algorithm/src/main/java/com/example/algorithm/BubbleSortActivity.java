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
public class BubbleSortActivity extends Activity {
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

        tvTitle.setText("冒泡排序");


        tvContent.setText("基本思想：\n" +
                "两个数比较大小，较大的数下沉，较小的数冒起来。\n" +
                "\n" +
                "算法描述：\n" +
                "•比较相邻的元素。如果第一个比第二个大，就交换它们两个；\n" +
                "•对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对，这样在最后的元素应该会是最大的数；\n" +
                "•针对所有的元素重复以上的步骤，除了最后一个；\n" +
                "•重复步骤1~3，直到排序完成。");

        tvCode.setText(" 代码实现：\n" +
                " public static int[] bubbleSort(int[] array) {\n" +
                "      for (int i = 0; i < array.length; i++)\n" +
                "          for (int j = 0; j < array.length - 1 - i; j++)\n" +
                "              if (array[j + 1] < array[j]) {\n" +
                "                  int temp = array[j + 1];\n" +
                "                  array[j + 1] = array[j];\n" +
                "                  array[j] = temp;\n" +
                "              }\n" +
                "      return array;\n" +
                "  }");


        int[] data = initData(8);
        tvResult.setText("结果：\n" +
                "数组：" + Arrays.toString(data) + "\n");
        int[] dataResult = bubbleSort(data);
        tvResult.setText(tvResult.getText().toString() +
                "排序：" + Arrays.toString(dataResult) + "\n");


        ImageView imageView = findViewById(R.id.iv_gif);
        String url="file:///android_asset/bubbleSort.gif";
        Glide.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                float totalWidth = ScreenUtils.getScreenWidth(BubbleSortActivity.this)-ScreenUtils.dip2px(BubbleSortActivity.this, 20);
                float totalHeight = totalWidth * 1f / resource.getWidth() * resource.getHeight();
                //背景图片
                LinearLayout.LayoutParams bgLayoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                bgLayoutParams.width = (int) totalWidth;
                bgLayoutParams.height = (int) totalHeight;
                imageView.setLayoutParams(bgLayoutParams);
                Glide.with(BubbleSortActivity.this)
                        .load(url)
                        .into(imageView);
            }
        });
    }

    // 该方法随机生成1-100的随机数
    private static int[] initData(int length) {
        int[] data = new int[length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Random().nextInt(100);
        }
        return data;
    }


    //冒泡排序
    public static int[] bubbleSort(int[] array) {
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array.length - 1 - i; j++)
                if (array[j + 1] < array[j]) {
                    int temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                }
        return array;
    }
}
