package com.xixia.aiimageupload.opcv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xixia.aiimageupload.R;


public class IniyializeActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION_CODE = 1;
    Bitmap photo;
    String uripath;

    ImageView iv_initialize_camera;
    ImageView iv_initialize_photo;
    ImageView go_to_face;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_initialize);

        //第一次时获得权限
        get_SDcard_permission();

        iv_initialize_camera = (ImageView) findViewById(R.id.iv_initialize_camera);
        iv_initialize_photo = (ImageView) findViewById(R.id.iv_initialize_photo);
        go_to_face = (ImageView) findViewById(R.id.iv_initialize_face);
        go_to_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFace();
            }
        });


        iv_initialize_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProcess_camera();
            }
        });

        iv_initialize_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProcess_photo();
            }
        });
    }

    //获得手机相机和图册的权限
    public void get_SDcard_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(IniyializeActivity.this, Manifest.permission.CAMERA);
            int checkWriteSDPermission = ContextCompat.checkSelfPermission(IniyializeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED || checkWriteSDPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(IniyializeActivity.this, new String[]{Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                return;
            } else {
            }
        } else {
        }
    }


    public void goToFace() {
        Intent intent = new Intent(IniyializeActivity.this, FaceActivity.class);
        startActivity(intent);
    }


    public void goToProcess_camera() {
        Intent intent = new Intent(IniyializeActivity.this, ProcessActivity.class);
        String camera_permission = "camera_permission";
        intent.putExtra("permission", camera_permission);
        startActivity(intent);
    }

    public void goToProcess_photo() {
        Intent intent = new Intent(IniyializeActivity.this, ProcessActivity.class);
        String file_permission = "file_permission";
        intent.putExtra("permission", file_permission);
        startActivity(intent);
    }


}


