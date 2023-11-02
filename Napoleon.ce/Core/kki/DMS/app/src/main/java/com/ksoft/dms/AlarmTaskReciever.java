package com.ksoft.dms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmTaskReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmTaskService.class);
        i.putExtra(TaskEdit.ITEM_ID, intent.getStringExtra(TaskEdit.ITEM_ID));

        if (Build.VERSION.SDK_INT < 26)
            context.startService(i);
        else
            context.startForegroundService(i);
    }
}
