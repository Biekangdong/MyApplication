package com.xixia.aiimageupload.icc;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.xixia.aiimageupload.PictureSelectorUtils;
import com.xixia.aiimageupload.R;
import com.xixia.aiimageupload.meitu.GlideEngine;
import com.xixia.aiimageupload.opcv.OpenCVActivity;

import java.io.File;
import java.util.List;

public class ICCActivity extends AppCompatActivity {
    private static final String TAG = "ICCActivity";
    private ImageView ivImage1;
    private ImageView ivImage2;

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icc);
        ivImage1 = (ImageView) findViewById(R.id.iv_image1);
        ivImage2 = (ImageView) findViewById(R.id.iv_image2);

    }

    public void upload(View view) {
        permissionChecker();
    }
    /**
     * load All Data
     */
    private void permissionChecker() {
        //String permission= Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU? Manifest.permission.READ_MEDIA_IMAGES:Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (PermissionChecker.checkSelfPermission(this, permission)) {
            initPhotoPickerSingle();
        } else {
            PermissionChecker.requestPermissions(this, new String[]{permission}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }
    private void initPhotoPickerSingle() {
        PictureSelectorUtils.initPhotoPickerSingle(this, new PictureSelectorUtils.OnPictureSelectorListener() {
            @Override
            public void selectResult(String mfilePath) {
                filePath = mfilePath;
                Log.e(TAG, "onResult: " + filePath);
                Glide.with(ICCActivity.this).load(filePath).into(ivImage1);
            }
        });
    }

    public void iccClick(View view) {
        if (filePath == null || !new File(filePath).exists()) {
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        String resultFilePath = ICCUtils.ICCJaiCreate(this, filePath);
        if (resultFilePath != null) {
            Glide.with(ICCActivity.this).load(resultFilePath).into(ivImage2);
        }
    }
    



}