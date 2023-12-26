package com.xixia.aiimageupload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xixia.aiimageupload.facedetector.FaceDetectorActivity;
import com.xixia.aiimageupload.icc.ICCActivity;
import com.xixia.aiimageupload.meitu.MeituAiActivity;
import com.xixia.aiimageupload.opcv.FaceCamera2Activity;
import com.xixia.aiimageupload.camera2.FaceCamera3Activity;
import com.xixia.aiimageupload.camera1.FaceCameraActivity;
import com.xixia.aiimageupload.opcv.OpenCVActivity;
import com.xixia.aiimageupload.pixels.BitmapPixelsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void meituClick(View view) {
        startActivity(new Intent(this, MeituAiActivity.class));
    }

    public void iccClick(View view) {
        startActivity(new Intent(this, ICCActivity.class));

    }

    public void OpenCVClick(View view) {
        startActivity(new Intent(this, OpenCVActivity.class));
    }

    public void pixelsClick(View view) {
        startActivity(new Intent(this, BitmapPixelsActivity.class));
    }

    public void faceDetectorClick(View view) {
        startActivity(new Intent(this, FaceDetectorActivity.class));
    }

    public void faceCameraClick(View view) {
        Intent intent = new Intent(this, FaceCameraActivity.class);
        startActivity(intent);
    }

    public void faceCamera2Click(View view) {
        Intent intent = new Intent(this, FaceCamera2Activity.class);
        startActivity(intent);
    }
    public void faceCamera3Click(View view) {
        Intent intent = new Intent(this, FaceCamera3Activity.class);
        startActivity(intent);
    }
}