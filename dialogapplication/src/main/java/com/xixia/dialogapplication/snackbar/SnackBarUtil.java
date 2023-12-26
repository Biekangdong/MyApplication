package com.xixia.dialogapplication.snackbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.xixia.dialogapplication.R;

/**
 * @ClassName SnackBarUtil
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/6/22 20:09
 * @Version 1.0
 * @UpdateDate 2023/6/22 20:09
 * @UpdateRemark 更新说明
 */
public class SnackBarUtil {
    //自定义 SnackBar 布局
    public static void show(Activity activity, View view, String msg, String action, SnackBarOnClick listener) {
        //获取示例 findViewById(android.R.id.content) //LENGTH_LONG/LENGTH_SHORT: 会自动消失 LENGTH_INDEFINITE: 需要手动点击消失
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        //设置 Snackbar 的深度，避免被其他控件遮挡
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbar.getView().setElevation(0);
        }
        //设置背景透明，避免自带黑色背景影响
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        //设置padding 取消自定义时黑色边框
        snackbar.getView().setPadding(0, 0, 0, 0);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        //设置SnackBar的显示位置
        //ViewGroup.LayoutParams layoutParams = snackbarLayout.getLayoutParams();
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(dip2px(activity,260),dip2px(activity,32)); // 将原来Snackbar的宽高传入新的LayoutParams
        flp.gravity = Gravity.CENTER; // 设置显示位置
        flp.bottomMargin = dip2px(activity,8);
        ((View) snackbarLayout).setLayoutParams(flp);
        //获取自定义布局
        View inflate = LayoutInflater.from(activity).inflate(R.layout.snackbar_view, null);
        //获取布局内控件
        TextView textView = inflate.findViewById(R.id.textView);
        //TextView 前边添加图片
        //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);//图片自己选择
        //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        //textView.setCompoundDrawables(drawable, null, null, null);
        //增加文字和图标的距离
        //textView.setCompoundDrawablePadding(20);
        //设置文本
        textView.setText(msg);
        if (action != null && listener != null) {
            TextView textViewSub = inflate.findViewById(R.id.textViewSub);
            textViewSub.setVisibility(View.VISIBLE);
            textViewSub.setText(action);
            textViewSub.setOnClickListener(v -> {
                if (listener != null) {
                    listener.clickEvent(snackbar);
                }
            });
        }
        //添加图片 获取布局内控件
        //ImageView imageView = inflate.findViewById(R.id.imageView2);
        //获取图片资源
        //Drawable drawable = activity.getResources().getDrawable(closeIcon);
        //设置图片
        //imageView.setImageDrawable(drawable);
        //将自定义布局添加到 Snackbar 中
        snackbarLayout.addView(inflate);
        //显示
        snackbar.show();
    }

    public interface SnackBarOnClick {
        void clickEvent(Snackbar snackbar);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Activity activity, float dpValue) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
