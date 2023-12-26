
package com.xixia.dialogapplication.popwindow;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.xixia.dialogapplication.R;

/**
 * Created by admin on 2019/9/2.
 */

public class TestPopupWindow extends PopupWindow {
    private Activity mContext;


    public TestPopupWindow(Activity context) {
        super(context);
        this.mContext = context;
        //获取布局文件
        View mContentView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_test, null);
        //设置布局
        setContentView(mContentView);
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置可以点击外部消息
        //开始
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

//        /***
//     * 在android7.0上，如果不主动约束PopuWindow的大小，比如，设置布局大小为 MATCH_PARENT,那么PopuWindow会变得尽可能大，以至于 view下方无空间完全显示PopuWindow，而且view又无法向上滚动，此时PopuWindow会主动上移位置，直到可以显示完全。
//     *　解决办法：主动约束PopuWindow的内容大小，重写showAsDropDown方法：
//     * @param anchor
//     */
//    @Override
//    public void showAsDropDown(View anchor,int xoff,int yoff,int gravity) {
//        if (Build.VERSION.SDK_INT >= 24) {
//            Rect visibleFrame = new Rect();
//            anchor.getGlobalVisibleRect(visibleFrame);
//            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
//            setHeight(height);
//            showAsDropDown(anchor, xoff, yoff,gravity);
//        } else {
//           showAsDropDown(anchor, xoff, yoff,gravity);
//        }
//        super.showAsDropDown(anchor);
//    }


}
