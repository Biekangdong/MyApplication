package io.jayx.aidlservice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

import io.jayx.aidlservice.IMyAidlInterface;
import io.jayx.aidlservice.R;
import io.jayx.aidlservice.service.MyService;

public class MainActivity extends AppCompatActivity {
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvName = (TextView) findViewById(R.id.tv_name);
        //启动服务
        Intent intent = new Intent();
        intent.setPackage("io.jayx.aidlservice");
        intent.setAction("io.jayx.aidlservice.service.MyService");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                try {
                    tvName.setText(iMyAidlInterface.getName());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }
}