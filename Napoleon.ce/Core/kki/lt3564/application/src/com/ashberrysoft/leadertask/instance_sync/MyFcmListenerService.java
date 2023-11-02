package com.ashberrysoft.leadertask.instance_sync;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.utils.LTPowerManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static long mLastTimeNotify = 0;
    private static int id = 456813;

    public MyFcmListenerService() {

    }


    @Override
    public void onMessageReceived(RemoteMessage message){
        Map data = message.getData();
        final Context mContext = getApplicationContext();

        // проверка на залогиненость
//        android.util.Log.v("Tedorius","1");
        if (LTSettings.getInstance().getUserProfile().isValid() && !LTSettings.getInstance().isAutonomyMode()) {
//            android.util.Log.v("Tedorius","2");
            if (data.get("title").toString().equals("Новое поручение")) {
//                android.util.Log.v("Tedorius","3");
                long nowTime = System.currentTimeMillis();
                final Intent intent = SlidingActivity.newInstance(mContext);
                final PendingIntent pending = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder = new Notification.Builder(mContext, TaskNotifyHelper.CHANNEL_ID);
                } else {
                    builder = new Notification.Builder(mContext);
                }

                builder.setContentTitle(mContext.getString(R.string.new_assignment));
                builder.setTicker(mContext.getString(R.string.app_name));
                builder.setSmallIcon(R.drawable.notification_icon);
                //builder.setWhen(nowTime);
                builder.setContentIntent(pending);
                builder.setContentText(data.get("body").toString());
                builder.setAutoCancel(true);

                if (nowTime > mLastTimeNotify + 3000) {
                    TaskNotifyHelper.setSound(getApplicationContext(), builder);
                }
                mLastTimeNotify = nowTime;
                id++;

                NotificationManager nm = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @SuppressLint("WrongConstant") NotificationChannel ch = new NotificationChannel(TaskNotifyHelper.CHANNEL_ID, "LeaderTask", NotificationManager.IMPORTANCE_HIGH);
                    nm.createNotificationChannel(ch);
                }

                Notification noti = null;

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                    noti = builder.getNotification();
                else
                    noti = builder.build();

                nm.notify(id, noti);
//                android.util.Log.v("Tedorius","4");
            }
        }
    }

}
