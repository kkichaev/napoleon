package com.ashberrysoft.leadertask.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Synchronization service.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class SyncService extends Service {

    private static SyncAdapter sSyncAdapter = null;

    public SyncService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (sSyncAdapter == null) {
            sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
