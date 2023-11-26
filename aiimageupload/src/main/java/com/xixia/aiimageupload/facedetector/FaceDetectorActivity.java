package com.xixia.aiimageupload.facedetector;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.xixia.aiimageupload.PictureSelectorUtils;
import com.xixia.aiimageupload.R;

/**
 * @ClassName OpenCVActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/9/9 17:45
 * @Version 1.0
 * @UpdateDate 2023/9/9 17:45
 * @UpdateRemark 更新说明
 */
public class FaceDetectorActivity extends Activity {
    private static final String TAG = "FaceDetectorActivity";
    private ImageView ivImage1;
    private ImageView ivImage2;
    private ProgressBar progressbar;


    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detector);

        ivImage1 = (ImageView) findViewById(R.id.iv_image1);
        ivImage2 = (ImageView) findViewById(R.id.iv_image2);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);


    }

    public void selectClick(View view) {
        permissionChecker();
    }


    /**
     * 检查权限
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

    /**
     * 选择图片
     */
    private void initPhotoPickerSingle() {
        PictureSelectorUtils.initPhotoPickerSingle(this, new PictureSelectorUtils.OnPictureSelectorListener() {
            @Override
            public void selectResult(String mfilePath) {
                filePath = mfilePath;
                Glide.with(FaceDetectorActivity.this).load(filePath).into(ivImage1);
//                String base64String = imageToBase64(new File(filePath));
//                Log.e(TAG, "onResult: " + base64String);

            }
        });
    }

//    /**
//     * 将图片转换成Base64编码的字符串
//     */
//    public static String imageToBase64(File file) {
//        InputStream is = null;
//        byte[] data = null;
//        String result = null;
//        try {
//            is = new FileInputStream(file);
//            //创建一个字符流大小的数组。
//            data = new byte[is.available()];
//            //写入数组
//            is.read(data);
//            //用默认的编码格式进行编码
//            result = Base64.encodeToString(data, Base64.DEFAULT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != is) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        return result;
//    }


    public void faceClick(View view) {
        progressbar.setVisibility(View.VISIBLE);
        new SavePicAsyncTask().execute(filePath);
    }


    /**
     * 人脸检测
     */
    private int numberOfFace = 1;       //最大检测的人脸数
    private FaceDetector myFaceDetect;  //人脸识别类的实例
    private FaceDetector.Face[] myFace; //存储多张人脸的数组变量
    int numberOfFaceDetected;       //实际检测到的人脸数
    float myEyesDistance;           //两眼之间的距离
    private int imageWidth, imageHeight;

    private Bitmap detectFace(Bitmap bitmap) {
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();

        //识别
        int numberOfFace = 1;
        myFace = new FaceDetector.Face[numberOfFace];       //分配人脸数组空间
        myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);
        numberOfFaceDetected = myFaceDetect.findFaces(bitmap, myFace);    //FaceDetector 构造实例并解析人脸
        if (numberOfFaceDetected > 0) {
            //绘制人脸框
            return onDrawFaceBitmap(bitmap);
        } else {
            return null;
        }

    }

    protected Bitmap onDrawFaceBitmap(Bitmap myBitmap) {
        Bitmap bitmapRoot = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmapRoot);
        canvas.drawBitmap(myBitmap, 0, 0, null);    //画出位图
        Paint myPaint = new Paint();
        myPaint.setColor(Color.GREEN);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(3);          //设置位图上paint操作的参数

        for (int i = 0; i < numberOfFaceDetected; i++) {
            FaceDetector.Face face = myFace[i];
            PointF myMidPoint = new PointF();
            face.getMidPoint(myMidPoint);
            myEyesDistance = face.eyesDistance();   //得到人脸中心点和眼间距离参数，并对每个人脸进行画框
            canvas.drawRect(            //矩形框的位置参数
                    (int) (myMidPoint.x - myEyesDistance),
                    (int) (myMidPoint.y - myEyesDistance),
                    (int) (myMidPoint.x + myEyesDistance),
                    (int) (myMidPoint.y + myEyesDistance),
                    myPaint);
        }

        return bitmapRoot;
    }


    /**
     * 保存图片
     */
    class SavePicAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String filePath = strings[0];
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;  //构造位图生成的参数，必须为565
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            Bitmap resultBitmap = detectFace(bitmap);
            return resultBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressbar.setVisibility(View.GONE);
            if (bitmap != null) {
                //绘制人脸框
                Toast.makeText(FaceDetectorActivity.this, "识别到人脸", Toast.LENGTH_SHORT).show();
                ivImage2.setImageBitmap(bitmap);
            } else {
                Toast.makeText(FaceDetectorActivity.this, "未识别到人脸", Toast.LENGTH_SHORT).show();
                Glide.with(FaceDetectorActivity.this).load(filePath).into(ivImage2);
            }

        }

    }
}
