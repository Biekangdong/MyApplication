package com.example.camera.decode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzer;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.mlsdk.common.MLFrame;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * @ClassName HuaweiHmsUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-10-14 18:23
 * @Version 1.0
 * @UpdateDate 2023-10-14 18:23
 * @UpdateRemark 更新说明
 */
public class HuaweiHmsUtils {
    public static final int BITMAP_CODE = 333;
    public static final int MULTIPROCESSOR_SYN_CODE = 444;
    public static int mode=MULTIPROCESSOR_SYN_CODE;
    public static int type=HmsScan.ALL_SCAN_TYPE;
    /**
     * Call the MultiProcessor API in synchronous mode.
     */
    public static String decode(int width, int height, byte[] data,Activity activity) {
        HmsScan[] hmsScans=null;

        Bitmap bitmap = convertToBitmap(width, height, data);
        if (mode == BITMAP_CODE) {
            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).setPhotoMode(false).create();
            hmsScans= ScanUtil.decodeWithBitmap(activity, bitmap, options);
        } else if (mode == MULTIPROCESSOR_SYN_CODE) {
            MLFrame image = MLFrame.fromBitmap(bitmap);
            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).create();
            HmsScanAnalyzer analyzer = new HmsScanAnalyzer(options);;
            SparseArray<HmsScan> result = analyzer.analyseFrame(image);
            if (result != null && result.size() > 0 && result.valueAt(0) != null && !TextUtils.isEmpty(result.valueAt(0).getOriginalValue())) {
                hmsScans = new HmsScan[result.size()];
                for (int index = 0; index < result.size(); index++) {
                    hmsScans[index] = result.valueAt(index);
                }
            }
        }
        if(hmsScans!=null&&hmsScans.length>0){
            return hmsScans[0].getOriginalValue();
        }else {
            return null;
        }
    }

    /**
     * Convert camera data into bitmap data.
     */
    private static Bitmap convertToBitmap(int width, int height, byte[] data) {
        YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);
    }

    /**
     * Call the MultiProcessor API in asynchronous mode.
     */
    private void decodeAsyn(int width, int height, byte[] data, final Activity activity, int type) {
        final Bitmap bitmap = convertToBitmap(width, height, data);
        MLFrame image = MLFrame.fromBitmap(bitmap);
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).create();
        HmsScanAnalyzer analyzer = new HmsScanAnalyzer(options);
        analyzer.analyzInAsyn(image).addOnSuccessListener(new OnSuccessListener<List<HmsScan>>() {
            @Override
            public void onSuccess(List<HmsScan> hmsScans) {
                if (hmsScans != null && hmsScans.size() > 0 && hmsScans.get(0) != null && !TextUtils.isEmpty(hmsScans.get(0).getOriginalValue())) {
                    HmsScan[] infos = new HmsScan[hmsScans.size()];
                    Message message = new Message();
                    message.obj = hmsScans.toArray(infos);
                    //sendMessage(message);
                } else {
                }
                bitmap.recycle();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                bitmap.recycle();
            }
        });
    }
}
