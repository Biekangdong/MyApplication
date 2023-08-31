package com.juai.usbmanager;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;

/**
 * created by WY on 2021/3/6.
 */
public class SoftKeyBoardListener {

    /**
     * activity的根视图
     */
    private View rootView;
    /**
     * 纪录根视图的显示高度
     */
    int rootViewVisibleHeight;
    private OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;

    public static int keyBoardHeight = 0;

    public SoftKeyBoardListener(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        //获取activity的根视图
        rootView = weakReference.get().getWindow().getDecorView();

        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取当前根视图在屏幕上显示的大小
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int visibleHeight = r.height();
                keyBoardHeight = visibleHeight;
                System.out.println(""+visibleHeight);
//                if (rootViewVisibleHeight == 0) {
//                    rootViewVisibleHeight = visibleHeight;
//                    return;
//                }

                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (rootViewVisibleHeight == visibleHeight) {
                    return;
                }

                //根视图显示高度变小超过200，可以看作软键盘显示了
                if (rootViewVisibleHeight - visibleHeight > 200) {
                    if (onSoftKeyBoardChangeListener != null) {
                        onSoftKeyBoardChangeListener.keyBoardShow(rootViewVisibleHeight - visibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

                //根视图显示高度变大超过200，可以看作软键盘隐藏了
                if (visibleHeight - rootViewVisibleHeight > 200) {
                    if (onSoftKeyBoardChangeListener != null) {
                        onSoftKeyBoardChangeListener.keyBoardHide(visibleHeight - rootViewVisibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

            }
        });
    }

    private void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
    }

    public interface OnSoftKeyBoardChangeListener {
        void keyBoardShow(int height);

        void keyBoardHide(int height);
    }

    public static void setListener(Activity activity, OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        SoftKeyBoardListener softKeyBoardListener = new SoftKeyBoardListener(weakReference.get());
        softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener);
    }

}
