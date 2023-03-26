package nickgao.com.viewpagerswitchexample.view;

import android.annotation.TargetApi;
import android.os.Build;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * 卡片效果Transformer
 * @author:gaoyoujian
 */

public class CardTransformer implements ViewPager.PageTransformer {

    private int mOffset = 40;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void transformPage(View page, float position) {

        if (position <=0) {
            page.setTranslationX(-position * page.getWidth());
            //缩放卡片并调整位置
            float scale = (page.getWidth() + mOffset * position) / page.getWidth();
            page.setScaleX(scale);
            page.setScaleY(scale);
            //移动Y轴坐标
            page.setTranslationY(-position * mOffset);
            page.setTranslationZ(position);
        }

    }

}
