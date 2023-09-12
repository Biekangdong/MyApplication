package com.xixia.aiimageupload.opcv;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xixia.aiimageupload.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class ProcessActivity extends AppCompatActivity {
    private CascadeClassifier cascadeClassifier;
    private static final String SumiaoString = "Sumiao";
    private static final String GrayString = "Gray";
    private static final String thresholdString = "threshold";
    private static final String LunkuoString = "Lunkuo";
    private static final String HuaijiuString = "Huaijiu";
    private static final String LianhuanhuaFString = "LianhuanhuaF";
    private static final String BingDongString = "Bingdong";
    private static final String RongzhuString = "Rongzhu";
    private static final String FudiaoString = "Fudiao";



    private  Bitmap photo;
    private  String uripath;
    private  Photo_Function face;
    private  Photo_class pc;
    private  Message ms;
    Bitmap src;
    private ProgressDialog pd;

    private ImageView iv_process_photo;
    private ImageButton iv_process_contrast;
    private ImageView iv_process_save;
    private  Button button_initial;
    private  Button button_sketch;
    private    Button button_Lunkuo;
    private    Button button_Fudiao;
    private    Button button_Gray;
    private    Button button_Theshold;
    private   Button button_Lianhuanhua;
    private  Button button_Huaijiu;
    private  Button button_Bingdong;
    private  Button button_Rongzhu;
    private  Button button_face;
    private   ImageView iv_process_back;



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.actiity_process);

        init();
        show_initial_photo();
        Listener();
    }

    private void  init(){
        src = null;
        face = new Photo_Function();

        iv_process_photo = (ImageView) findViewById(R.id.iv_process_photo);
        iv_process_save = (ImageView)findViewById(R.id.iv_process_save);
        iv_process_contrast = (ImageButton)findViewById(R.id.iv_process_contrast);
        button_initial = (Button)findViewById(R.id.button_initial);
        button_Bingdong = (Button)findViewById(R.id.button_frozen);
        button_Fudiao = (Button)findViewById(R.id.button_embossment);
        button_Gray = (Button)findViewById(R.id.button_gray);
        button_Huaijiu= (Button)findViewById(R.id.button_reminiscence);
        button_sketch = (Button)findViewById(R.id.button_sketch);
        button_Rongzhu= (Button)findViewById(R.id.button_casting);
        button_Lunkuo =  (Button)findViewById(R.id.button_outline);
        button_Theshold = (Button)findViewById(R.id.button_binaryzation);
        button_Lianhuanhua = (Button)findViewById(R.id.button_cartoon);
        iv_process_back = (ImageView) findViewById(R.id.iv_process_back);
        button_face= (Button)findViewById(R.id.button_face_recognition);

    }

    private void Listener(){

        iv_process_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {ProcessActivity.this.finish();}
        });

        iv_process_save.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Dialog dia = new AlertDialog.Builder(ProcessActivity.this)
                                .setMessage("确定保存当前图片吗？")
                                .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                                            public void onClick(DialogInterface dia,int whichButton){
                                                                saveImage();
                                                            }})
                                .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dia,int whichButton){}
                                }).create();
                dia.show();
//                saveImage();
            }
            });

        iv_process_contrast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP://松开事件发生后执行代码的区域
                        iv_process_photo.setImageBitmap(src);
                        break;
                    case MotionEvent.ACTION_DOWN://按住事件发生后执行代码的区域
                        src = convertViewToBitmap();
                        iv_process_photo.setImageBitmap(photo);
                        break;
                    default:
                        break;
                }
                return true;
            }});

        button_initial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_process_photo.setImageBitmap(photo);
            }
        });

        button_face.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_process_photo.setImageBitmap(Face_Detect(photo));
            }
        });

        button_sketch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_Sumiao_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    SumiaoF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_Sumiao_photo());
            }
        });

        button_Theshold.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_theshold_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    ThesF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_theshold_photo());
            }
        });

        button_Lunkuo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_Lunkuo_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    LunKuoF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_Lunkuo_photo());
            }
        });

        button_Rongzhu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_Rongzhu_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    RongzhuF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_Rongzhu_photo());
            }
        });

        button_Gray.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_Gray_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    GrayF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_Gray_photo());
            }
        });

        button_Fudiao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_FuDiao_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    FudiaoF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_FuDiao_photo());
            }
        });

        button_Bingdong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_FuDiao_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    BingDongF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_FuDiao_photo());
            }
        });

        button_Lianhuanhua.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_LianHuanHua_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    LianhuanhuaF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_LianHuanHua_photo());
            }
        });

        button_Huaijiu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pc.get_Huaijiu_photo()==null){
                    pd = ProgressDialog.show(ProcessActivity.this, "", "加载滤镜中，请稍后……");
                    HuaijiuF();
                }
                else
                    iv_process_photo.setImageBitmap(pc.get_Huaijiu_photo());
            }
        });

    }

    private void show_initial_photo(){
        String permission = getIntent().getStringExtra("permission");
        if(permission.equalsIgnoreCase("camera_permission"))
            OpenCamrea();
        else if (permission.equalsIgnoreCase("file_permission"))
            openFile();
//        if (photo==null)
//            ProcessActivity.this.finish();
    }

    public void OpenCamrea(){
        //查看内存卡状态
        String Envior = Environment.getExternalStorageState();
        if (Envior.equals(Environment.MEDIA_MOUNTED)) {

            photo =null;
            Intent ie1 = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(ie1,1);
        }
        else
            Toast.makeText(ProcessActivity.this,"内存不可用",Toast.LENGTH_LONG).show();
    }

    public void openFile(){

        photo =null;
        Intent ie2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(ie2,2);

    }

    public  void saveImage(){
        Bitmap bmp = convertViewToBitmap();
        File appDir = new File(Environment.getExternalStorageDirectory(), "photo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(file.getPath()))));
        Toast.makeText(ProcessActivity.this,"保存成功",Toast.LENGTH_LONG).show();
    }


    public Bitmap convertViewToBitmap() {
        iv_process_photo.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(iv_process_photo.getDrawingCache());
        iv_process_photo.setDrawingCacheEnabled(false);
        iv_process_photo.setImageBitmap(bitmap);
        return bitmap;
    }

    private Bitmap getScaleBitmap(String wallpaperPath) {
        Bitmap bm = BitmapFactory.decodeFile(wallpaperPath);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth=dm.widthPixels;
        /**
         * 图片的宽度 （用于判断当前图判是否过大，避免OOM使得imageview无法显示）
         * 适用于 720P，其他分辨率需要重新修改
         */
        int mFixedWidth = 2870;
        //图片的高度 （用于客户预置特别大图，重新处理bitmap）
        int mFixedHeight = 1920;
        if(bm.getWidth()<=screenWidth){
            return bm;
        }else{
            //Bitmap bmp=Bitmap.createScaledBitmap(bm, screenWidth, bm.getHeight()*screenWidth/bm.getWidth(), true);
            if (bm.getWidth()<= mFixedWidth) {
                return bm;
            }
            Bitmap bmp=Bitmap.createScaledBitmap(bm, bm.getWidth()*mFixedHeight/bm.getHeight(), mFixedHeight, true);
            return bmp;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(data!=null){
            switch (requestCode){
                case 1:{
                    if(data.getData() != null || data.getExtras() != null){
                        Uri uri = data.getData();
                        if(uri != null){
                            uripath = uri.getPath();
                            this.photo = BitmapFactory.decodeFile(uri.getPath());
                        }
                        if (photo == null) {
                            Bundle bundle = data.getExtras();
                            if (bundle !=null) {
                                photo = (Bitmap) bundle.get("data");
                            }
                        }
                    }
                    pc = new Photo_class(photo);
                    iv_process_photo.setImageBitmap(photo);
                    break;
                }

                case 2:{
                    //打开相册并选择照片，这个方式选择单张
                    // 获取返回的数据，这里是android自定义的Uri地址
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // 获取选择照片的数据视图
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    // 从数据视图中获取已选择图片的路径
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    // 将图片显示到界面上
//                    Toast.makeText(ProcessActivity.this,picturePath,Toast.LENGTH_LONG).show();

                    photo = getScaleBitmap(picturePath);
                    pc = new Photo_class(photo);
                    iv_process_photo.setImageBitmap(photo);

//                    iv_process_photo.setImageBitmap(face.RGB2Gray(photo));
                    break;
                }
                default:
                    break;
            }
        }

    }

//*-------------------------------------------------------------------------------------------------*//


    private Bitmap Face_Detect(Bitmap photo){
        Mat srcmap = new Mat();
        Mat graymap = new Mat();
        MatOfRect faces = new MatOfRect();
        int FaceSize = (int)(photo.getWidth()*0.2);
        Utils.bitmapToMat(photo,srcmap);
        Imgproc.cvtColor(srcmap,graymap,Imgproc.COLOR_RGBA2RGB);
        if (cascadeClassifier != null ){
            cascadeClassifier.detectMultiScale(graymap,faces,1.1, 2, 2,
                    new Size(FaceSize,FaceSize),new Size());
        }
        else {
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i <facesArray.length; i++)
            Imgproc.rectangle(srcmap, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

        Bitmap result = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.RGB_565);;
        Utils.matToBitmap(srcmap,result);
        return result;
    }

    private void SumiaoF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_Sumiao_photo(face.SuMiao(photo));
                ms = new Message();
                ms.obj = SumiaoString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void GrayF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_Gray_photo(face.RGB2Gray(photo));
                ms = new Message();
                ms.obj = GrayString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void ThesF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_put_theshold_photo(face.theshold(photo));
                ms = new Message();
                ms.obj = thresholdString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void LunKuoF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_Lunkuo_photo(face.Lunkuo(photo));
                ms = new Message();
                ms.obj = LunkuoString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void HuaijiuF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_Huaijiu_photo(face.HuaiJiu(photo));
                ms = new Message();
                ms.obj = HuaijiuString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void LianhuanhuaF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_LianHuanHua_photo(face.LianHuanHua(photo));
                Message ms = new Message();
                ms.obj =LianhuanhuaFString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void RongzhuF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_Rongzhu_photo(face.RongZhu(photo));
                ms = new Message();
                ms.obj =RongzhuString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void BingDongF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_BingDong_photo(face.BingDong(photo));
                ms = new Message();
                ms.obj =BingDongString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private void FudiaoF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                pc.put_FuDiao_photo(face.FuDiao(photo));
                ms = new Message();
                ms.obj =FudiaoString;
                handler.sendMessage(ms);// 执行耗时的方法之后发送消给handler
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.obj==SumiaoString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_Sumiao_photo());
            }
            else if(msg.obj==BingDongString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_BingDong_photo());
            }
            else if(msg.obj==thresholdString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_theshold_photo());
            }
            else if(msg.obj==LianhuanhuaFString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_LianHuanHua_photo());
            }
            else if(msg.obj==LunkuoString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_Lunkuo_photo());
            }
            else if(msg.obj==RongzhuString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_Rongzhu_photo());
            }
            else if(msg.obj==GrayString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_Gray_photo());
            }
            else if(msg.obj==FudiaoString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_FuDiao_photo());
            }
            else if(msg.obj==HuaijiuString) {
                pd.dismiss();
                iv_process_photo.setImageBitmap(pc.get_Huaijiu_photo());
            }
        }
    };

//*-------------------------------------------------------------------------------------------------*//

    private void initializeOpenCVDependencies() {
        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();


            // Load the get
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.e("log_wons", "OpenCV init error");
        }
        initializeOpenCVDependencies();
    }
}
