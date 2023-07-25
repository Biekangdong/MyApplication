package com.xixia.dialogapplication.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xixia.dialogapplication.R;

import java.util.List;

/**
 * @ClassName CustomerAdapter
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/6/22 18:00
 * @Version 1.0
 * @UpdateDate 2023/6/22 18:00
 * @UpdateRemark 更新说明
 */
public class CustomerAdapter extends BaseAdapter {
    private Context mContext;
    private List<FruitBean> mList;

    public CustomerAdapter(Context mContext, List<FruitBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fruit_list, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(mList.get(position).name);
        return convertView;
    }

    class ViewHolder {
        public TextView tvName;

    }
}
