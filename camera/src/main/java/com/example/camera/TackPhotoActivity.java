package com.example.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lensun.lensuncustomizpro.R;

/**
 * @ClassName TackPhotoActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-10-28 12:59
 * @Version 1.0
 * @UpdateDate 2023-10-28 12:59
 * @UpdateRemark 更新说明
 */
public class TackPhotoActivity extends AppCompatActivity {
    private ImageView ivImage;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tack_photo);
        ivImage = (ImageView) findViewById(R.id.iv_image);
    }

    //权限请求
    public final int REQUEST_CAMERA_PERMISSION = 1;
    private String cameraPermission = Manifest.permission.CAMERA;
    private  String storePermission=Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU?Manifest.permission.READ_MEDIA_IMAGES:Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private void checkCameraPermission() {
        //检查是否有相机权限
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, storePermission) != PackageManager.PERMISSION_GRANTED) {
            //没权限，请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,storePermission},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            //有权限
            TakePhotoUtils.startOpenCameraImage(this);
        }
    }

    //权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意权限
                    TakePhotoUtils.startOpenCameraImage(this);
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    Toast.makeText(this, "拒绝权限，请去设置里面手动开启权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    public void tackphoto(View view) {
        checkCameraPermission();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakePhotoUtils.REQUEST_CAMERA:
                    String realPath= TakePhotoUtils.dispatchHandleCamera(this);
                    ivImage.setImageBitmap(BitmapFactory.decodeFile(realPath));
                    break;
                default:
                    break;
            }
        }
    }



}
