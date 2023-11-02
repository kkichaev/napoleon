package com.ashberrysoft.leadertask.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, LeaderTaskSyncService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(startServiceIntent);
        } else {
            context.startService(startServiceIntent);
        }
    }
}
