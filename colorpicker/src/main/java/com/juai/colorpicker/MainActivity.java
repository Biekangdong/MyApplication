package com.juai.colorpicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.DecimalUtil;
import com.skydoves.colorpickerview.FadeUtils;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorCoordinateListener;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.TouchPixelListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ColorPickerView colorPickerView;
    private FrameLayout flSurfaceView;

    private PreviewSurfaceView previewSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        colorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);
        flSurfaceView = (FrameLayout) findViewById(R.id.fl_surface_view);


        initColorPickerView();
    }


    private void initColorPickerView() {
//        int hueStep = 0;
//        int hue = 0;
//        int satStep = 0;
//        int sat = 0;
//        hsvArray = new float[3];
//        hsvArray[0] = hue;
//        hsvArray[1] = sat / 100f;
//        hsvArray[2] = 1.0f;
//        seekbarSue.setProgress(hue);
//        seekbarSat.setProgress(sat);
//        String HString = DecimalUtil.decimal(String.valueOf(hsvArray[0]));
//        String SString = DecimalUtil.decimal(String.valueOf(hsvArray[1] * 100));
//        tvHText.setText("H: " + HString);
//        tvSText.setText("S: " + SString + "%");


        colorPickerView.getSelector().setVisibility(View.VISIBLE);
        colorPickerView.setSelectorDrawable(ContextCompat.getDrawable(this, R.mipmap.location));
        colorPickerView.setSelectorIsShow(true);
        colorPickerView.getSelector().setVisibility(View.GONE);
        BubbleFlag bubbleFlag = new BubbleFlag(mContext);
        bubbleFlag.setFlagMode(FlagMode.FADE);
        colorPickerView.setFlagView(bubbleFlag, true);
        BrightnessSlideBar brightnessSlideBar = new BrightnessSlideBar(mContext);
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);
        AlphaSlideBar alphaSlideBar = new AlphaSlideBar(mContext);
        colorPickerView.attachAlphaSlider(alphaSlideBar);
        colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this, R.mipmap.test2));


        colorPickerView.setColorListener(
                new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        int selectedColor = envelope.getColor();
//                        GradientDrawable myShape = (GradientDrawable) viewColorBg.getBackground();
//                        myShape.setColor(selectedColor);
                        float[] hsvArray = colorPickerView.getBrightnessSlider().getHsv();

                        float hueValue = hsvArray[0];
                        float satValue = hsvArray[1];
                        String HString = DecimalUtil.decimal(String.valueOf(hsvArray[0]));
                        String SString = DecimalUtil.decimal(String.valueOf(hsvArray[1] * 100));
//                        tvHText.setText("H: " + HString);
//                        tvSText.setText("S: " + SString + "%");

                        if(previewSurfaceView!=null){
                            previewSurfaceView.setTouchXY((int) colorPickerView.mappedPoints[0], (int) colorPickerView.mappedPoints[1]);

                        }
                    }
                });


        colorPickerView.setTouchPixelListener(new TouchPixelListener() {
            @Override
            public void onPointDownSelected(double x, double y) {

                showSurfaceView();
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) flSurfaceView.getLayoutParams();
//                layoutParams.width = dip2px(100);
//                layoutParams.height = dip2px(100);
//                flSurfaceView.setLayoutParams(layoutParams);
                FadeUtils.fadeIn(flSurfaceView);

            }

            @Override
            public void onPointUpSelected() {
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) flSurfaceView.getLayoutParams();
//                layoutParams.width = 0;
//                layoutParams.height = 0;
//                flSurfaceView.setLayoutParams(layoutParams);
                FadeUtils.fadeOut(flSurfaceView);
            }
        });
    }

    private void showSurfaceView() {
        if (previewSurfaceView == null) {
            previewSurfaceView = new PreviewSurfaceView(this);
            previewSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(dip2px(100), dip2px(100)));
            flSurfaceView.addView(previewSurfaceView);
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}