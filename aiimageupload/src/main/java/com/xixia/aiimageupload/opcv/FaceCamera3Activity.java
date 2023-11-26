package com.xixia.aiimageupload.opcv;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xixia.aiimageupload.Camera2Utils;
import com.xixia.aiimageupload.R;

import java.util.ArrayList;
import java.util.List;

public class FaceCamera3Activity extends Activity {
    private TextureView textureView;
    private Button change;

    private String[] permissions = {Manifest.permission.CAMERA};
    private List<String> permissionList = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_camera3);
        textureView = (TextureView) findViewById(R.id.textureView);
        change = (Button) findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //初始化
        Camera2Utils.getInstance().init(getWindowManager(), this, textureView);
        //动态授权
        getPermission();
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (!permissionList.isEmpty()) {
                //进行授权
                ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
            } else {
                textureView.setSurfaceTextureListener(textureListener);
            }
        }
    }

    //只能写在Activity中，下次把授权写到activity中，减少麻烦
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length != 0) {
                //表示有权限没有授权
                getPermission();
            } else {
                //表示都授权
                textureView.setSurfaceTextureListener(textureListener);

            }
        }
    }

    /*SurfaceView状态回调*/
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Camera2Utils.getInstance().startPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
}