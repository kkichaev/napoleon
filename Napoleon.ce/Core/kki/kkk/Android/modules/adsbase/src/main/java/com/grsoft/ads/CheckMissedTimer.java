package com.grsoft.ads;

import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.dataobjects.TaskQuery;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.preference.PreferenceManager;


public class CheckMissedTimer extends Timer{
	private AdsService service;
	private CheckMissedTimerTask task = new CheckMissedTimerTask();
	
	public CheckMissedTimer(AdsService service, long d) {
		this.service = service;
		schedule(task, d, d);
	}
	
	class CheckMissedTimerTask extends TimerTask{
		SQLiteStatement stm;
		
		public CheckMissedTimerTask(){
			try{
				DbWriter.checkDBTable(TaskQuery.class);
				String tn = DataObjectInfo.getInstance().getTableName(TaskQuery.class);
				SQLiteDatabase db = DataBaseManager.getDataBase();
				stm = db.compileStatement("select count(rowid) from " + tn + " where solution = 0");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try{
				if(stm != null){
					long c = stm.simpleQueryForLong();
					
					if(c > 0)
						showNotify();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void showNotify() {
		Intent a = new Intent(service, NewTaskList.class); 
        PendingIntent contentIntent = PendingIntent.getActivity(service, 0, a, 0);
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(service);
		String sound = pref.getString(SettingNotify.TASKMISSED, "content://settings/system/notification_sound");

		Notification.Builder  builder = new Notification.Builder(service)
         .setContentText(service.getString(R.string.taskmissed))
         .setSmallIcon(R.drawable.new_order)
         .setAutoCancel(true)
         .setContentIntent(contentIntent)
         .setSound(Uri.parse(sound));
        
		
        NotificationManager nm = (NotificationManager)service.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification noti = null;
		NotificationChannel channel = null;

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

		nm.notify(R.id.taskmissed, noti);
	}
}
