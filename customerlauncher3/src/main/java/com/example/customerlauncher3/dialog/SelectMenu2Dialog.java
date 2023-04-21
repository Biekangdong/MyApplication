package com.example.customerlauncher3.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerlauncher3.R;
import com.example.customerlauncher3.adapter.SelectMenu2Adapter;
import com.example.customerlauncher3.bean.SelectMenuBean;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/12/4.
 * 确认dialog
 */

public class SelectMenu2Dialog extends Dialog {
    private DialogViewListener listener;
    private Activity mContext;

    private TextView tvTitle;
    private RecyclerView rclView;
    private TextView tvCancel;

    private List<SelectMenuBean>  menuList=new ArrayList<>();
    private String title;

    private int titleColor;
    private int sureColor;


    public interface DialogViewListener {
        void sureClick(int positon);
    }

    public interface DialogViewCancleListener {
        void cancleClick();
    }

    public SelectMenu2Dialog(Activity context) {
        super(context);
        mContext = context;
    }

    public void setListener(DialogViewListener listener) {
        this.listener = listener;
    }

    public SelectMenu2Dialog(Activity context, int themeResId, List<SelectMenuBean>  menuList) {
        super(context, themeResId);
        mContext = context;
        this.menuList = menuList;
    }
    public SelectMenu2Dialog(Activity context, int themeResId, List<SelectMenuBean>  menuList, String title, int titleColor, int sureColor) {
        super(context, themeResId);
        mContext = context;
        this.menuList = menuList;
        this.title = title;
        this.titleColor = titleColor;
        this.sureColor = sureColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_menu, null);
        setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        //设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.transparent);
        //解决弹出被底部导航栏遮挡问题
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //消除边距
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        //设置Dialog点击外部消失
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.8);
//        lp.width = ScreenUtils.dip2px(mContext,300);
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        lp.y =  ScreenUtils.dip2px(mContext,50);
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        rclView = (RecyclerView) findViewById(R.id.rcl_view);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);


        initRclAdapter();

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if(titleColor!=0){
            tvTitle.setTextColor(mContext.getResources().getColor(titleColor));
        }
        if(sureColor!=0){
            tvCancel.setTextColor(mContext.getResources().getColor(sureColor));
        }

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    //初始化adapter
    private void initRclAdapter() {
        rclView.setLayoutManager(new LinearLayoutManager(mContext));
        rclView.setNestedScrollingEnabled(false);
        SelectMenu2Adapter selectMenuAdapter = new SelectMenu2Adapter(mContext, R.layout.item_select_menu2, menuList);
        selectMenuAdapter.setData(menuList);
        rclView.setAdapter(selectMenuAdapter);
        rclView.setItemAnimator(null);
        selectMenuAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                cancel();
                if(listener!=null){
                    listener.sureClick(position);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }
}
