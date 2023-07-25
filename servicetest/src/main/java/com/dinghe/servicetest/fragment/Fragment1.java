package com.dinghe.servicetest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dinghe.servicetest.R;

/**
 * @ClassName Fragment1
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/16 22:23
 * @Version 1.0
 * @UpdateDate 2023/5/16 22:23
 * @UpdateRemark 更新说明
 */
public class Fragment1 extends BaseFragment implements View.OnClickListener {
    private TextView tvText;

    int type;
    String categoryId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         type = getArguments().getInt("type");
         categoryId = getArguments().getString("categoryId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);

        return view;
    }

    //设置数据
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                startToFragment(getActivity(),R.id.fl_content,new Fragment2());
                break;
        }
    }
}
