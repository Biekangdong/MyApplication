package com.xixia.aiimageupload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xixia.aiimageupload.icc.ICCActivity;
import com.xixia.aiimageupload.meitu.MeituAiActivity;
import com.xixia.aiimageupload.opcv.OpenCVActivity;
import com.xixia.aiimageupload.pixels.BitmapPixelsActivity;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

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

}