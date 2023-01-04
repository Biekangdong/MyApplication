package com.demo.coordinatorlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @ClassName DataAdapter
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2022/9/23 17:31
 * @Version 1.0
 * @UpdateDate 2022/9/23 17:31
 * @UpdateRemark 更新说明
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.VM> {
    private Context context;
    private List<String> mList;

    public DataAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public VM onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
        return new DataAdapter.VM(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VM holder, int position) {
        holder.tvContent.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class VM extends RecyclerView.ViewHolder {
        private TextView tvContent;



        public VM(@NonNull View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
