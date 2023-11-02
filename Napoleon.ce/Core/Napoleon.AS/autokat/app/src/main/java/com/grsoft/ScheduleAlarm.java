package com.grsoft;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.grsoft.napoleon.Main;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;

public class ScheduleAlarm extends BroadcastReceiver {
    public static final String TEXT = "text";
    public static int id = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent a = new Intent(context, MainActivity.class);
        a.putExtra(MainActivity.OPEN_SCHEDULE, true);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, a,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentText(intent.getStringExtra(TEXT))
                .setSmallIcon(R.drawable.napoleon)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Notification nt = null;
        NotificationChannel channel = null;
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        final String chID = "channel_notify_id";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(chID, chID, NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
            builder.setChannelId(chID);
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            nt = builder.getNotification();
        else
            nt = builder.build();

        nm.notify(id++, nt);
    }
}
