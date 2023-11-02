package com.grsoft.ads;

import java.text.SimpleDateFormat;

import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.napoleon.dataobjects.impl.TaskImpl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class TaskNotify extends BroadcastReceiver {
	public static int id = R.id.tasknotify;

	@Override
	public void onReceive(Context context, Intent intent) {
		String taskid = intent.getStringExtra(AdsConsts.TASKID); 
		Intent a = new Intent(context, TaskPreview.class); 
		a.putExtra(AdsConsts.TASKID, taskid);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, a, 0);
      
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String sound = pref.getString(SettingNotify.TASKNOTIFY, "content://settings/system/notification_sound");
      
		Notification.Builder builder = new Notification.Builder(context)
				.setContentText(buildText(taskid))
				.setSmallIcon(R.drawable.notify)
				.setAutoCancel(true)
				.setContentIntent(contentIntent)
				.setSound(Uri.parse(sound));
		
		Notification noti = null;
		NotificationChannel channel = null;
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			channel = new NotificationChannel(AdsService.DEFAULT_CHANNEL_ID,
					AdsService.DEFAULT_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
			nm.createNotificationChannel(channel);
			builder.setChannelId(AdsService.DEFAULT_CHANNEL_ID);
		}

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			noti = builder.getNotification();
		else
			noti = builder.build();
		
		nm.notify(id++, noti);
		
		Log.d(this.getClass().getCanonicalName(), 
				String.format("alarm fired taskid = %s", taskid));
	}

	private String buildText(String taskid) {
		String result = ""; 
		TaskImpl ti = new TaskImpl();
		if (ti.read("taskid", taskid)) {
			TaskQuery t = ti.getData();
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			
			StringBuilder sb = new StringBuilder();
			sb.append(sdf.format(t.start));
			sb.append(" ");
			sb.append(t.text.substring(0, t.text.length() > 100 ? 100 : t.text.length()));
			
			result = sb.toString();
		}
		
		return result;
	}
}
