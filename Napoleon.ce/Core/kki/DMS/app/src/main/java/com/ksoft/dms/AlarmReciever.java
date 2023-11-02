package com.ksoft.dms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmService.class);
        i.putExtra(AlarmDlg.MESSAGE, intent.getStringExtra(AlarmDlg.MESSAGE));
        i.putExtra(NoteItemEdit.ITEM_ID, intent.getStringExtra(NoteItemEdit.ITEM_ID));

        if (Build.VERSION.SDK_INT < 26)
            context.startService(i);
        else
            context.startForegroundService(i);
    }
}
