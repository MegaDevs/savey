package com.megadevs.savey.machineserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SocketService extends Service {

    private SaveyServerSocket mSocket;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mSocket == null) {
            mSocket = new SaveyServerSocket();
            mSocket.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSocket.onDestroy();
    }
}
