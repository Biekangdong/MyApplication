package com.xixia.dialogapplication.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
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
public class SystemDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("系统弹窗")
                .setMessage("信息")
                //.setIcon(R.drawable.assign_set_question_ic_v2)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "确认", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        return dialog;
    }
}
