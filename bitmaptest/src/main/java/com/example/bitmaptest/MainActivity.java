package com.example.bitmaptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Bitmap 编辑RGB颜色
 * 技术点：
 * 1，存储权限请求 requestPermissions
 * 2，选择系统相册 openAlbum
 * 3，异步任务保存 AsyncTask
 * 4，线程池编辑 ExecutorService
 * 5，颜色矩阵 ColorMatrix
 * 6，生成指定分辨率图像 setDpi
 */
public class MainActivity extends AppCompatActivity {
    private ImageView ivImage;
    private Button btnSelect;
    private Button btnSave;
    private LinearLayout llSeekbarLayout;
    private TextView tvColorR;
    private SeekBar sbColorR;
    private TextView tvColorG;
    private SeekBar sbColorG;
    private TextView tvColorB;
    private SeekBar sbColorB;

    private Bitmap currentBitmap;
    private Bitmap copyBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivImage = (ImageView) findViewById(R.id.iv_image);
        btnSelect = (Button) findViewById(R.id.btn_select);
        btnSave = (Button) findViewById(R.id.btn_save);
        llSeekbarLayout = (LinearLayout) findViewById(R.id.ll_seekbar_layout);
        tvColorR = (TextView) findViewById(R.id.tv_color_r);
        sbColorR = (SeekBar) findViewById(R.id.sb_color_r);
        tvColorG = (TextView) findViewById(R.id.tv_color_g);
        sbColorG = (SeekBar) findViewById(R.id.sb_color_g);
        tvColorB = (TextView) findViewById(R.id.tv_color_b);
        sbColorB = (SeekBar) findViewById(R.id.sb_color_b);

        setOnViewLickListener();
    }

    //监听点击事件
    public void setOnViewLickListener() {
        //选择相册
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });
        //保存图片
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentBitmap==null){
                    return;
                }
                if(copyBitmap==null){
                    copyBitmap=currentBitmap;
                }
                new StickerTask().execute(copyBitmap);
            }
        });
        sbColorR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int progress,
                                          boolean fromUser) {
                setRGBMatrix();
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });
        sbColorG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int progress,
                                          boolean fromUser) {
                setRGBMatrix();
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });
        sbColorB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int progress,
                                          boolean fromUser) {
                setRGBMatrix();
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    //申请读写权限
    private void requestPermissions() {
        //申请一个运行时权限处理
        //权限WRITE_EXTERNAL_STORAGE表示同时授予程序对SD卡读和写的能力。
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {//授予了权限后，则调用openAlbum方法来获取图片
            openAlbum();
        }
    }

    //申请读写权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //选择相册
    private void openAlbum() {
        // 使用意图直接调用手机相册
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 打开手机相册,设置请求码
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case 1://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        currentBitmap = BitmapFactory.decodeFile(path);
                        ivImage.setImageBitmap(currentBitmap);
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    //保存指定分辨率图片
    public File saveToImage(Bitmap bitmap) {
        FileOutputStream fos;
        try {
            // SD卡根目录
            File dir = getExternalFilesDir("print");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File picFile = new File(dir, System.currentTimeMillis() + ".jpg");
            fos = new FileOutputStream(picFile);
            ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray);

            byte[] imageData = imageByteArray.toByteArray();

            //300 will be the dpi of the bitmap
            setDpi(imageData, 400);

            fos.write(imageData);
            fos.flush();
            fos.close();
            bitmap.recycle();
            return picFile;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //设置图片分辨率
    public void setDpi(byte[] imageData, int dpi) {
        imageData[13] = 1;
        imageData[14] = (byte) (dpi >> 8);
        imageData[15] = (byte) (dpi & 0xff);
        imageData[16] = (byte) (dpi >> 8);
        imageData[17] = (byte) (dpi & 0xff);
    }


    //合成预览图片
    public class StickerTask extends AsyncTask<Bitmap, Void, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "开始保存", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected File doInBackground(Bitmap... bitmaps) {
            File savePath = saveToImage(bitmaps[0]);
            return savePath;
        }

        @Override
        protected void onCancelled(File file) {
            super.onCancelled(file);

        }

        @Override
        protected void onPostExecute(File result) {
            super.onPostExecute(result);
            Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }


    //RGB颜色修改

    /**
     * //RBG颜色监听
     * if (preBitmap != null) {
     * float progressR = sb_red.getProgress() / 128f;//这里处理后值的范围为[0,2]，初始值是1，也即初始时不做任何改变。
     * float progressG = sb_green.getProgress() / 128f;
     * float progressB = sb_blue.getProgress() / 128f;
     * float progressA = sb_brightness.getProgress() / 128f;
     * float progressS = sb_saturation.getProgress() / 128f;
     * float[] values = new float[]
     * { progressR, 0, 0, 0, 0,
     * 0, progressG, 0, 0, 0,
     * 0, 0, progressB, 0, 0,
     * 0, 0, 0, progressA, 0 };
     * //1、改变色相及亮度
     * rgbMatrix.set(values);
     * //2、改变饱和度、透明度、灰度。当饱和度=0时，会变成黑白图片（有灰度的黑白照片）。
     * huduMatrix.setSaturation(progressS);
     * //3、要想将色彩三元素综合运用到一张图片上，需要通过颜色矩阵的postConcat方法将三元素进行连接。
     * colorMatrix.reset();//要重置一下才可以，不然等于是在之前的基础上进行的更改
     * colorMatrix.postConcat(rgbMatrix);//这里其实是将两个矩阵进行了运算，熟悉矩阵运算的应该知道，先后顺序是有重大影响的
     * colorMatrix.postConcat(huduMatrix);
     * //4、通过颜色滤镜将颜色矩阵应用于图片上
     * paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
     * canvas.drawBitmap(preBitmap, new Matrix(), paint);
     * imageView.setImageBitmap(afterBitmap);
     * }
     */
    public void setRGBMatrix() {
        tvColorR.setText(String.valueOf((int) (sbColorR.getProgress() / 255f * 100)));
        tvColorG.setText(String.valueOf((int) (sbColorG.getProgress() / 255f * 100)));
        tvColorB.setText(String.valueOf((int) (sbColorB.getProgress() / 255f * 100)));

        executeSyncBitmapEditRunnable();

    }

    //图片处理
    private Bitmap getResultBitmap() {
        if (currentBitmap == null) {
            return null;
        }
        //这里处理后值的范围为[0,2]，初始值是1，也即初始时不做任何改变。
        float progressR = sbColorR.getProgress() / 128f;
        float progressG = sbColorG.getProgress() / 128f;
        float progressB = sbColorB.getProgress() / 128f;
        float[] values = new float[]
                {progressR, 0, 0, 0, 0,
                        0, progressG, 0, 0, 0,
                        0, 0, progressB, 0, 0,
                        0, 0, 0, 2, 0};

        ColorMatrix rgbMatrix = new ColorMatrix();
        rgbMatrix.set(values);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(rgbMatrix));
        //创建画布
        Bitmap copyBitmap = Bitmap.createBitmap(currentBitmap.getWidth(), currentBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(copyBitmap);
        //在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
        canvas.drawBitmap(currentBitmap, 0, 0, paint);

        return copyBitmap;
    }

    //线程池处理图片
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private void executeSyncBitmapEditRunnable() {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                copyBitmap = getResultBitmap();
                myHandler.sendEmptyMessage(1);
            }
        });
    }

    //
    MyHandler myHandler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //显示
                ivImage.setImageBitmap(copyBitmap);
            }
        }
    }
}