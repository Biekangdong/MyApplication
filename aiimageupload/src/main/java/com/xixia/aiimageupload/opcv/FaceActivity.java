package com.xixia.aiimageupload.opcv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xixia.aiimageupload.R;

public class FaceActivity extends AppCompatActivity {

    private ImageView iv_face_back;
    private ImageView iv_face_save;
    private ImageView left_image_view;
    private ImageView right_image_view;
    private ImageView exchange_image_view;
    private Bitmap left_bitmap = null;
    private Bitmap right_bitmap = null;
    private Bitmap left_result_bitmap = null;
    private Bitmap right_result_bitmap = null;





    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_face);

        iv_face_back = (ImageView) findViewById(R.id.iv_face_back);
        iv_face_save = (ImageView) findViewById(R.id.iv_face_save);
        left_image_view = (ImageView) findViewById(R.id.left_image_view);
        right_image_view = (ImageView) findViewById(R.id.right_image_view);
        exchange_image_view = (ImageView) findViewById(R.id.face_changeface);
        iv_face_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceActivity.this.finish();
            }
        });
        left_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(1);
            }
        });
        right_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(2);
            }
        });
        exchange_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("SendMessage", "exchange");
                sendTwoImagesToServer();
            }
        });



        iv_face_save.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
//                Toast.makeText(FaceActivity.this,"????",Toast.LENGTH_LONG).show();
                Dialog dia = new AlertDialog.Builder(FaceActivity.this)
                        .setMessage("确定保存当前图片吗？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dia,int whichButton){
                                saveImage();
                            }})
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dia,int whichButton){}
                        }).create();
                dia.show();
            }
        });

    }


    public void openFile(int code){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, code);

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
        Toast.makeText(FaceActivity.this,"保存成功",Toast.LENGTH_LONG).show();
    }


    public Bitmap convertViewToBitmap() {
        right_image_view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(right_image_view.getDrawingCache());
        right_image_view.setDrawingCacheEnabled(false);
        right_image_view.setImageBitmap(bitmap);
        return bitmap;
    }



    public void sendTwoImagesToServer(){
        new Thread(){
            public void run() {
                try {
                    if ((left_bitmap == null) || (right_bitmap == null)) {
                        Log.v("SendMessage", "bitmap is null");
                        return;
                    }
                    Socket socket = new Socket("192.168.199.151", 6666);
                    Log.v("SendMessage", "connect");

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    left_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bout);
                    out.write(bout.toByteArray());
                    String s = "xiapingping";

                    out.write(s.getBytes());
                    //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img2);
                    bout = new ByteArrayOutputStream();
                    right_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bout);
                    out.write(bout.toByteArray());
                    out.write("Send End".getBytes());
                    Log.v("SendMessage", "send");
                    out.flush();

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    Log.v("SendMessage", "recive1");
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    Log.v("SendMessage", "recive2");
                    byte[] buffer = new byte[1024];
                    Log.v("SendMessage", "recive3");
                    int len = -1;
                    Log.v("SendMessage", "recive4");
                    while((len = in.read(buffer, 0, 1024)) != -1){
                        Log.v("SendMessage", "0");
                        System.out.println(len);
                        outputStream.write(buffer, 0, len);
                    }
                    Log.v("SendMessage", "recive5");
                    System.out.println(outputStream.toByteArray().length);
                    left_result_bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.toByteArray().length);
                    // bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    Log.v("SendMessage", "recive6");
                    //show_image.setImageBitmap(bmp);
                    handler.sendEmptyMessage(0x123);
                    Log.v("SendMessage", "recive7");

                } catch (IOException e) {
                    handler.sendEmptyMessage(0x124);

                    Log.v("SendMessage", "error connect");
                    e.printStackTrace();
                }
            }
        }.start();
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



    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0x123){
                right_image_view.setImageBitmap(left_result_bitmap);
                //right_image_view.setImageBitmap(right_result_bitmap);
            }
            if(msg.what == 0x124){
                Toast.makeText(getApplicationContext(), "error connect!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(data!=null){
            switch (requestCode){
                case 1:{
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
                    left_bitmap = getScaleBitmap(picturePath);
                    left_image_view.setImageBitmap(left_bitmap);
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
                    right_bitmap = getScaleBitmap(picturePath);
                    right_image_view.setImageBitmap(right_bitmap);

//                    iv_process_photo.setImageBitmap(face.RGB2Gray(photo));
                    break;
                }
                default:
                    break;
            }
        }
    }

}
