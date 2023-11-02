package com.ksoft.dms;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.Html;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.ksoft.dms.database.controller.TaskController;
import com.ksoft.dms.database.entity.Task;
import com.ksoft.dms.database.entity.TaskItem;

public class AlarmTaskService extends IntentService{
    private static final String DEFAULT_CHANNEL_ID = "default_chanel";

    public AlarmTaskService() {
        super("AlarmTaskService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Task task = new TaskController(getApplicationContext()).read(intent.getStringExtra(TaskEdit.ITEM_ID));

        if (task == null)
            return;

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(Html.fromHtml(taskInfo(task), 0));
        builder.setSmallIcon(R.drawable.outline_calculate_24);
        builder.setAutoCancel(false);
        Intent i = new Intent(this, Tasks.class);
        i.putExtra(TaskEdit.ITEM_ID, task.id);
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
        startForeground(task.alarmid, nc);
    }

    private String taskInfo(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.text);

        for(TaskItem i : task.items) {
            sb.append(" (");
            sb.append(i.pos + 1);
            sb.append(i.text);
            sb.append(")");
        }

        return sb.toString();
    }
}
