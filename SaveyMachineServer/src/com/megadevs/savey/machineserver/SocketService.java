package com.megadevs.savey.machineserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SocketService extends Service {

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SaveyServerSocket.getInstance().start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        SaveyServerSocket.getInstance().onDestroy();
    }
}
