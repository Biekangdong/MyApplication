package com.dong.voicechanger;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioRecordingConfiguration;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnVoice;

    private AudioRecord recorder,recorder2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnVoice.getText().toString().equals("录音")) {
                    btnVoice.setText("停止");
                    startRecord();
                } else {
                    btnVoice.setText("录音");
                    stopVoice();
                }

            }
        });


//        checkVoice();
        registerNetworkReceiver();
    }

    private void checkVoice() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            audioManager.registerAudioRecordingCallback(new AudioManager.AudioRecordingCallback() {
                @Override
                public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                    super.onRecordingConfigChanged(configs);
                    Log.e("aa", "onRecordingConfigChanged: " + (configs.isEmpty() ? "没占用" : "占用"));
                }
            }, null);
        }
    }

    private void startRecord() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.MICROPHONE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (recorder == null) {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                                    AudioFormat.CHANNEL_IN_MONO,
                                    AudioFormat.ENCODING_DEFAULT, 44100);
                        }


                        try {
                            recorder.startRecording();
                            if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                                Log.e("aa", "onRecordingConfigChanged: " + "没占用");
                            } else {
                                Log.e("aa", "onRecordingConfigChanged: " + "占用");
                            }
                        }catch (Exception e){
                            Log.e("aa", "onRecordingConfigChanged: " + "占用");

                        }
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {

                    }
                })
                .start();
    }

    private void stopVoice(){
        if(recorder!=null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }

    }


    //注册网络监听广播
    NetStatusReceiver netReceiver;
    public void registerNetworkReceiver(){
        if(netReceiver==null){
             netReceiver=new NetStatusReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netReceiver, filter);
        }
    }
    public void unRegisterNetworkReceiver(){
        if(netReceiver!=null){
            unregisterReceiver(netReceiver);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNetworkReceiver();
    }
}