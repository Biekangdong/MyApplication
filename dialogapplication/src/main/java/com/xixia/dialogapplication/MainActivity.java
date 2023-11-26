package com.xixia.dialogapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xixia.dialogapplication.dialog.CustomProgressDialog;
import com.xixia.dialogapplication.dialog.DialogActivity;
import com.xixia.dialogapplication.dialogfragment.DialogFragmentActivity;
import com.xixia.dialogapplication.floatwindows.FloatWindowsActivity;
import com.xixia.dialogapplication.snackbar.SnackBarActivity;
import com.xixia.dialogapplication.spinner.SpinnerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void dialog(View view) {
        startActivity(new Intent(this, DialogActivity.class));
    }



    public void spinner(View view) {
        startActivity(new Intent(this, SpinnerActivity.class));
    }

    public void snackbar(View view) {
        startActivity(new Intent(this, SnackBarActivity.class));

    }



    public void dialogfragment(View view) {
        startActivity(new Intent(this, DialogFragmentActivity.class));
    }
    public void floatwindows(View view) {
        startActivity(new Intent(this, FloatWindowsActivity.class));
    }


}