package com.ridesharedriver.app.online_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//status  service
public class OnlineStatus extends Service {
    public OnlineStatus() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


}