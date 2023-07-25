package io.jayx.aidlservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import io.jayx.aidlservice.IMyAidlInterface;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyAidlInterface();
    }

    class MyAidlInterface extends IMyAidlInterface.Stub{


        @Override
        public String getName() throws RemoteException {
            return "i'm yours";
        }

        @Override
        public void setName(String name) throws RemoteException {

        }
    }

}