package com.xixia.dialogapplication.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xixia.dialogapplication.R;

/**
 * @ClassName SystemDialogFagment
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/6/23 0:03
 * @Version 1.0
 * @UpdateDate 2023/6/23 0:03
 * @UpdateRemark 更新说明
 */
public class CustomerDialogFragment extends DialogFragment {

    public View mRootView;

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvCancel;
    private TextView tvSure;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            //获取布局
            mRootView = inflater.inflate(R.layout.dialog_confirm,container,false);
        }


        tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        tvContent = (TextView) mRootView.findViewById(R.id.tv_content);
        tvCancel = (TextView) mRootView.findViewById(R.id.tv_cancel);
        tvSure = (TextView) mRootView.findViewById(R.id.tv_sure);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "点击确定", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return mRootView;
    }

}
