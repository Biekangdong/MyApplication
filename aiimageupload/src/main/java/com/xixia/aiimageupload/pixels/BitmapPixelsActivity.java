package com.xixia.aiimageupload.pixels;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.xixia.aiimageupload.PictureSelectorUtils;
import com.xixia.aiimageupload.R;
import com.xixia.aiimageupload.icc.ICCUtils;

import java.io.File;

public class BitmapPixelsActivity extends AppCompatActivity {
    private static final String TAG = "ICCActivity";
    private ImageView ivImage1;
    private ImageView ivImage2;

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_pixels);
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
                Glide.with(BitmapPixelsActivity.this).load(filePath).into(ivImage1);
            }
        });
    }

    public void click2(View view) {
        if (filePath == null || !new File(filePath).exists()) {
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        new StickerTask().execute(filePath);
    }

    //合成预览图片
    public class StickerTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap = BitmapFactory.decodeFile(strings[0]);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];//保存所有的像素的数组，图片宽×高
            //遍历记录图片每个像素信息
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int color = bitmap.getPixel(x, y);
                    // 如果你想做的更细致的话 可以把颜色值的R G B 拿到做响应的处理
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    int a = Color.alpha(color);

                    if (a > 30) {
                        pixels[y * width + x] = Color.BLACK;
                    }
                }
            }
            //创建bitmap,设置像素数组
            Bitmap bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmapResult.setPixels(pixels, 0, width, 0, 0, width, height);

            return bitmapResult;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            ivImage2.setImageBitmap(result);
        }
    }
}