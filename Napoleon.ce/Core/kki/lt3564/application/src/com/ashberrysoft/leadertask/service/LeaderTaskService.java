package com.ashberrysoft.leadertask.service;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

public class LeaderTaskService extends Service {
    // =========================================================
    // Constants
    // =========================================================
    public static final String KEY_LOCAL = "local";
    public static final String KEY_REMOTE = "remote";
    public static final String KEY_ACTION = "action";

    // =========================================================
    // Class fields
    // =========================================================
    private Looper mServiceLooper;
    private ServiceHandler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Message msg = new Message();
        msg.arg1 = startId;
        msg.obj = intent;
        mHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        final HandlerThread thread = new HandlerThread("LeaderTaskServiceHandler",
        /* (Thread.MIN_PRIORITY + Thread.NORM_PRIORITY) / 2 */Process.THREAD_PRIORITY_DEFAULT);
        thread.start();
        mServiceLooper = thread.getLooper();
        mHandler = new ServiceHandler(mServiceLooper, this, ServiceConstants.RECIVE,
                ServiceConstants.ACTION_SERVICE_ERROR);

        // broadcast receiver for AlarmManager intent about post notification
        // mReceiver = new NotificationTimeReceiver();
        // IntentFilter filter = new IntentFilter(IPCConstants.ACTION_POST_NOTIFICATION);
        // registerReceiver(mReceiver, filter);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
        // unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}