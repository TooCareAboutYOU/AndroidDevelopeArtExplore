package com.developeartexplore.mode_ipc.main.binders;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BinderPoolService extends Service {

    public static final String TAG = "BinderPoolService";
    
    private Binder mBinderPool=new BinderPool.BinderPoolImpl();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service --> onCreate: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service --> onBind: ");

        return mBinderPool;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service --> onDestroy: ");
        super.onDestroy();

    }
}
