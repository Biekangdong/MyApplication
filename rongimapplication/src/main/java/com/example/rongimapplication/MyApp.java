package com.example.rongimapplication;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import io.rong.imkit.RongIM;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.UserDataProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * @ClassName MyApp
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/3/26 13:46
 * @Version 1.0
 * @UpdateDate 2023/3/26 13:46
 * @UpdateRemark 更新说明
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initRongIM();
    }

    private void initRongIM() {
        // 初始化融云IM
        String appKey = "sfci50a7sozli";
        RongIM.init(this, appKey);

        String token1 = "L2Rb54ykhnJmQvTsggzCX1ihzVRzmBfn@1blk.cn.rongnav.com;1blk.cn.rongcfg.com00";
        String token2 = "TPhpJ2nu+Y1mQvTsggzCX0/CmQod0IpN@1blk.cn.rongnav.com;1blk.cn.rongcfg.com";
        RongIMClient.connect(token1, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                Log.e("AAA", "onSuccess:userId: " + userId);
//                RongIM.setUserInfoProvider(new UserDataProvider.UserInfoProvider() {
//                    @Override
//                    public UserInfo getUserInfo(String userId) {
//                        UserInfo userInfo = new UserInfo(userId, "1", Uri.parse("1"));
//                        return userInfo;
//                    }
//                }, true);

                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
                if (userInfo != null) {
                    Log.e("AAA", "onSuccess:name: " + userInfo.getName());
                }
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode e) {
                Log.e("AAA", "onError: " + e.getValue());
            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {
                Log.e("AAA", "onDatabaseOpened: " + code);
            }
        });

        RongIMClient.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {

            /**
             * 连接状态返回回调
             * @param status 状态值
             */
            @Override
            public void onChanged(ConnectionStatus status) {
                Log.e("AAA", "ConnectionStatus: "+status);
                if(status==ConnectionStatus.CONNECTED){

                }
            }
        });

    }
}
