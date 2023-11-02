package com.ksoft.dms;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

public class AlarmService extends IntentService {


    private static final int NOTIFICATION_ID = 100;
    private static final String DEFAULT_CHANNEL_ID = "default_chanel";

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(intent.getStringExtra(AlarmDlg.MESSAGE));
        builder.setSmallIcon(R.drawable.outline_calculate_24);
        Intent i = new Intent(this, NoteItemEdit.class);
        i.putExtra(NoteItemEdit.ITEM_ID, intent.getStringExtra(NoteItemEdit.ITEM_ID));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        NotificationChannel channel = null;

        NotificationManagerCompat nm = NotificationManagerCompat.from(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                    DEFAULT_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
            builder.setChannelId(DEFAULT_CHANNEL_ID);
        }
        
        Notification nc = builder.build();
        startForeground(NOTIFICATION_ID, nc);
    }
}
