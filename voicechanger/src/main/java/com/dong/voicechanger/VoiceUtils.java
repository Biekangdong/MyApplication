package com.dong.voicechanger;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

/**
 * @ClassName VoiceUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2022/7/4 17:15
 * @Version 1.0
 * @UpdateDate 2022/7/4 17:15
 * @UpdateRemark 更新说明
 */
public class VoiceUtils {
    public static boolean validateMicAvailability(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);
        try{

            recorder.startRecording(); // 即使麦克风被占用，这里调用也不会抛异常。但如果没被占用，则录制状态会变成AudioRecord.RECORDSTATE_RECORDING

            if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                available = true;

            } else {
                available = false;
            }

        } finally{
            recorder.release();
            recorder = null;
        }

        return available;
    }
}
