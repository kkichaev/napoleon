package com.serviko.sales;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.serviko.database.DBHelper;

import java.util.Date;

public class FCMListener extends FirebaseMessagingService {
    public static String NEW_MSG_RECIEVED = "com.serviko.sales.new_msg_recieved";
    static int notifyId = 1;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null){
            RemoteMessage.Notification n = remoteMessage.getNotification();
            createNotification(n);

//            SQLiteDatabase db = new DBHelper(getApplicationContext()).getWritableDatabase();
//            ContentValues cv = new ContentValues();
//            cv.put("text", n.getBody());
//            cv.put("date", new Date().getTime());
//            cv.put("title", n.getTitle());
//            db.insert("message", null, cv);
        }

        getApplication().sendBroadcast(new Intent(NEW_MSG_RECIEVED));
    }

    void createNotification(RemoteMessage.Notification n) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R. drawable.ic_notify)
                .setColor(0xFF304F8F)
                .setContentTitle(n.getTitle())
                .setContentText(n.getBody())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(n.getBody()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notifyId++, builder.build());
    }


    @Override
    public void onNewToken(@NonNull String s) {
        MainActivity.appId = s;
    }
}
