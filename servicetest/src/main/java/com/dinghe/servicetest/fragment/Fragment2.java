package com.dinghe.servicetest.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class Fragment2 extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                popBackFragment();
                break;
        }
    }
}
