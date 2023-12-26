package com.xixia.aiimageupload.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.tools.ToastUtils;
import com.sunmi.camerascan.Config;
import com.sunmi.camerascan.Image;
import com.sunmi.camerascan.ImageScanner;
import com.sunmi.camerascan.Symbol;
import com.sunmi.camerascan.SymbolSet;
import com.xixia.aiimageupload.camera2.Camera2Utils;
import com.xixia.aiimageupload.R;
import com.xixia.aiimageupload.camera2.ScanConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FaceCamera3Activity extends Activity {
    private TextureView textureView;
    private TextureView faceTextureView;//用于标注人脸

    private String[] permissions = {Manifest.permission.CAMERA};
    private List<String> permissionList = new ArrayList();

    private ImageScanner mImageScanner;//声明扫描器

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_camera3);
        textureView = (TextureView) findViewById(R.id.textureView);
        faceTextureView= (TextureView) findViewById(R.id.facetextureView);
        initImageScanner();
        //初始化
        Camera2Utils.getInstance().init(getWindowManager(), this, textureView,faceTextureView);
        Camera2Utils.getInstance().setOnPreviewFrameListener(new Camera2Utils.OnPreviewFrameListener() {
            @Override
            public void previewFrame(byte[] data, int width, int height ,int faceCount) {
                decodeSynQrCode(data,width,height);
            }
        });
        //动态授权
        getPermission();
    }

    public void rotateClick(View view){
        Camera2Utils.getInstance().changeLens();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Camera2Utils.getInstance().closeCamera();
        destoryImageScanner();
    }

    /**
     * 商米扫码器
     */
    private void initImageScanner(){
        mImageScanner = new ImageScanner();//创建扫描器
        mImageScanner.setConfig(0, Config.X_DENSITY, 2);//行扫描间隔
        mImageScanner.setConfig(0, Config.Y_DENSITY, 2);//列扫描间隔
          //是否开启同一幅图一次解多个条码,0：只解一个，1：可解多个条码
//        mImageScanner.setConfig(Symbol.NONE, Config.ENABLE_MULTILESYMS, 0);//默认0: 只解一个
//        //是否开启/不开启识读相关条码码制
//        mImageScanner.setConfig(Symbol.EAN8, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.UPCE, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.UPCA, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.EAN13, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.ISBN10, Config.ENABLE, 0);//默认0：不开启，ISBN13,ISBN10不能同时设置为1
//        mImageScanner.setConfig(Symbol.ISBN13, Config.ENABLE, 0);//默认0：不开启，ISBN13,ISBN10不能同时设置为1
//        mImageScanner.setConfig(Symbol.CODE11, Config.ENABLE, 0);//默认0：不开启
//        mImageScanner.setConfig(Symbol.I25, Config.ENABLE, 0);//默认0：不开启
//        mImageScanner.setConfig(Symbol.CODE128, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.CODABAR, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.CODE39, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.CODE93, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.DATABAR, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.DATABAR_EXP, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);//默认1: 开启
//        mImageScanner.setConfig(Symbol.QRCODE, Config.ENABLE_INVERSE, 2);//0：normal, 1：only inverse，默认2：auto
//        mImageScanner.setConfig(Symbol.MicroQR, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.MicroQR, Config.ENABLE_INVERSE, 2);//0：normal, 1：only inverse，默认2：auto
//        mImageScanner.setConfig(Symbol.PDF417, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.PDF417, Config.ENABLE_INVERSE, 0);//暂不支持反色读取
//        mImageScanner.setConfig(Symbol.MicroPDF417, Config.ENABLE, 0);//默认0：不开启
//        mImageScanner.setConfig(Symbol.MicroPDF417, Config.ENABLE_INVERSE, 0);//暂不支持反色读取
//        mImageScanner.setConfig(Symbol.DataMatrix, Config.ENABLE, 1);//默认1：开启
//        mImageScanner.setConfig(Symbol.DataMatrix, Config.ENABLE_INVERSE, 2);//0：normal, 1：only inverse，默认2：auto
//        mImageScanner.setConfig(Symbol.AZTEC, Config.ENABLE, 0);//默认1：开启
//        mImageScanner.setConfig(Symbol.AZTEC, Config.ENABLE_INVERSE, 0);//0：normal, 1：only inverse，默认2：auto
//        mImageScanner.setConfig(Symbol.Hanxin, Config.ENABLE, 0);//默认0：不开启
//        mImageScanner.setConfig(Symbol.Hanxin, Config.ENABLE_INVERSE, 0);//0：normal, 1：only inverse，默认2：auto
    }

    /**
     * 商米扫码器扫码
     */
    private AtomicBoolean isRUN = new AtomicBoolean(false);
    private void decodeSynQrCode(byte[] data, int width, int height) {
        if (mImageScanner != null) {
            if (isRUN.compareAndSet(false, true)) {
                //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
                Image source = new Image(width, height, "Y800");
                //图片旋转了90度，将扫描框的TOP作为left裁剪
                source.setData(data);//填充数据

                ArrayList<HashMap<String, String>> result = new ArrayList<>();
                //解码，返回值为0代表失败，>0表示成功
                int dataResult = mImageScanner.scanImage(source);
                if (dataResult != 0) {
                    SymbolSet syms = mImageScanner.getResults();//获取解码结果
                    for (Symbol sym : syms) {
                        if (sym.getResult().length() > 10) {
                            HashMap<String, String> temp = new HashMap<>();
                            //码制，条码类型,如“EAN-8”
                            temp.put(ScanConfig.TYPE, sym.getSymbolName());
                            //结果，解码结果字符串
                            temp.put(ScanConfig.VALUE, sym.getResult());
                            result.add(temp);
                            if (!ScanConfig.IDENTIFY_MORE_CODE) {
                                break;
                            }
                        }
                    }
                    syms.destroy();
                    if (result.size() > 0) {
                        isRUN.set(true);
                        finish();
                        ToastUtils.s(this,result.get(0).get(ScanConfig.VALUE));
                    } else {
                        isRUN.set(false);
                    }
                } else {
                    isRUN.set(false);
                }

            }
        }
    }


    /**
     * 销毁扫码器
     */
    private void destoryImageScanner(){
        if (mImageScanner != null) {
            mImageScanner.destroy();
        }
    }

}