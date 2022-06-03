package com.example.algorithm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.FrameLayout;
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
public class InsertionSortActivity extends Activity {
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

        tvTitle.setText("插入排序");


        tvContent.setText("基本思想：\n" +
                "在要排序的一组数中，假定前n-1个数已经排好序，现在将第n个数插到前面的有序数列中，使得这n个数也是排好顺序的。如此反复循环，直到全部排好顺序\n" +
                "\n" +
                "算法描述：\n" +
                "•从第一个元素开始，该元素可以认为已经被排序；\n" +
                "•取出下一个元素，在已经排序的元素序列中从后向前扫描；\n" +
                "•如果该元素（已排序）大于新元素，将该元素移到下一位置；\n" +
                "•重复步骤3，直到找到已排序的元素小于或者等于新元素的位置；\n" +
                "•将新元素插入到该位置后；\n" +
                "•重复步骤2~5。\n");

        tvCode.setText(" 代码实现：\n" +
                "public static int[] insertionSort(int[] array) {\n" +
                "        int current;\n" +
                "        for (int i = 0; i < array.length - 1; i++) {\n" +
                "            current = array[i + 1];\n" +
                "            int preIndex = i;\n" +
                "            while (preIndex >= 0 && current < array[preIndex]) {\n" +
                "                array[preIndex + 1] = array[preIndex];\n" +
                "                preIndex--;\n" +
                "            }\n" +
                "            array[preIndex + 1] = current;\n" +
                "        }\n" +
                "        return array;\n" +
                "    }");


        int[] data = initData(8);
        tvResult.setText("结果：\n" +
                "数组：" + Arrays.toString(data) + "\n");
        int[] dataResult = insertSort(data);
        tvResult.setText(tvResult.getText().toString() +
                "排序：" + Arrays.toString(dataResult) + "\n");


        ImageView imageView = findViewById(R.id.iv_gif);
        String url="file:///android_asset/insertionSort.gif";
        Glide.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                float totalWidth = ScreenUtils.getScreenWidth(InsertionSortActivity.this)-ScreenUtils.dip2px(InsertionSortActivity.this, 20);
                float totalHeight = totalWidth * 1f / resource.getWidth() * resource.getHeight();
                //背景图片
                LinearLayout.LayoutParams bgLayoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                bgLayoutParams.width = (int) totalWidth;
                bgLayoutParams.height = (int) totalHeight;
                imageView.setLayoutParams(bgLayoutParams);
                Glide.with(InsertionSortActivity.this)
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


    //插入排序
    //取要插入的新元素，和前面已经拍好的序做比较，该元素（已排序）大于新元素，将该元素移到下一位置，否则就插入排好的元素后面
    public static int[] insertSort(int[] array) {
        for (int i = 0; i < array.length-1; i++) {
            int current = array[i+1];
            int preIndex = i;
            while (preIndex >= 0 && current < array[preIndex]) {
                array[preIndex + 1] = array[preIndex];//该元素（已排序）大于新元素，将该元素移到下一位置；
                preIndex--;
            }
            //preIndex终止在哪个索引，就插入到哪个位置
            array[preIndex+1] = current;
        }
        return array;
    }
}
