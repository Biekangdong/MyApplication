package com.dong.voicechanger;

/**
 * @ClassName NetStatusReceiver
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2022/7/9 9:16
 * @Version 1.0
 * @UpdateDate 2022/7/9 9:16
 * @UpdateRemark 更新说明
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class NetStatusReceiver extends BroadcastReceiver{

    public static final int NETSTATUS_INAVAILABLE = 0;
    public static final int NETSTATUS_WIFI = 1;
    public static final int NETSTATUS_MOBILE = 2;
    public static int netStatus = 0;
    public static boolean updateSuccess = false;
    private INetStatusListener mINetStatusListener;

    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo allNetInfo = cm.getActiveNetworkInfo();

        if (allNetInfo == null) {
            if (mobileNetInfo != null && (mobileNetInfo.isConnected() || mobileNetInfo.isConnectedOrConnecting())) {
                netStatus = NETSTATUS_MOBILE;
            } else if (wifiNetInfo != null && wifiNetInfo.isConnected() || wifiNetInfo.isConnectedOrConnecting()) {
                netStatus = NETSTATUS_WIFI;
            } else {
                netStatus = NETSTATUS_INAVAILABLE;
            }
        } else {
            if (allNetInfo.isConnected() || allNetInfo.isConnectedOrConnecting()) {
                if (mobileNetInfo.isConnected() || mobileNetInfo.isConnectedOrConnecting()) {
                    netStatus = NETSTATUS_MOBILE;
                } else {
                    netStatus = NETSTATUS_WIFI;
                }
            } else {
                netStatus = NETSTATUS_INAVAILABLE;
            }
        }
        if(mINetStatusListener != null){
            mINetStatusListener.getNetState(netStatus);
        }
//        if (netStatus == NETSTATUS_INAVAILABLE) {
//            Toast.makeText(context, "网络未连接",Toast.LENGTH_SHORT).show();
//        } else if (netStatus == NETSTATUS_MOBILE) {
//            Toast.makeText(context, "网络处于移动网络",Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, "网络处于Wifi网络",Toast.LENGTH_SHORT).show();
//        }
    }

    public void setNetStateListener(INetStatusListener listener){
        mINetStatusListener = listener;
    }

    public interface INetStatusListener{
        public void getNetState(int state);
    }
}