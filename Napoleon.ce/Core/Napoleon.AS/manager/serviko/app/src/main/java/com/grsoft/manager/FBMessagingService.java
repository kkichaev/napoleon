package com.grsoft.manager;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.RouteDeviation;

import java.util.List;

public class FBMessagingService extends  FirebaseMessagingService {
    int notifyID;

    static String NOFIFYID = "nid";
    static String PREF_NAME = "FBMessage";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        MainEx.setNewToken(token);

        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        notifyID = sp.getInt(NOFIFYID, 1);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        List<RouteDeviation> data = RouteDeviation.fromData(message.getData());
        if(data.size() > 0) {
            DbWriter w = new DbWriter();
            for(RouteDeviation ri : data) {
                w.insertRecord(ri);

                // Create an Intent for the activity you want to start
                Intent resultIntent = new Intent(this, AgentAlerts.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                String text = ri.getNotifyText(false);
                String title = ri.getTitle();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainEx.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_agent_alert)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        ;

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(notifyID++, builder.build());
            }
            w.close();
            SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putInt(NOFIFYID, notifyID);
            e.commit();
        }
    }
}
