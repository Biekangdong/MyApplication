package com.example.customerlauncher3.adapter;

import android.content.Context;


import com.example.customerlauncher3.R;
import com.example.customerlauncher3.bean.SelectMenuBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dong on 2020/1/21.
 */

public class SelectMenu2Adapter extends CommonAdapter<SelectMenuBean> {

    private Context mContext;
    private List<SelectMenuBean> mList = new ArrayList<>();

    public SelectMenu2Adapter(Context context, int layoutId, List<SelectMenuBean> data) {
        super(context, layoutId, data);
        mContext = context;
        mList.clear();
        mList.addAll(data);
    }

    public void setData(List<SelectMenuBean> data) {
        mList = data;
    }

    @Override
    protected void convert(ViewHolder holder, SelectMenuBean dataBean, int position) {
        holder.setText(R.id.tv_name, dataBean.getName());
        holder.setText(R.id.tv_name_sub, dataBean.getSubname());
        holder.setTextColor(R.id.tv_name, mContext.getResources().getColor(dataBean.getTextColor()));
    }

}