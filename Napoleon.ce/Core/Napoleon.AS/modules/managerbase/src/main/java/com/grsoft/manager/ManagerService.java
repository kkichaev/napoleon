package com.grsoft.manager;

import com.grsoft.dataobjects.impl.DivisionManagerImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ManagerService extends Service{
	public final static int NOTIFICATION = 1;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		createNotificationIcon();
		
		if (DivisionManagerImpl.isMobile())
			GPSUtilNew.start(this, Consts.ONE_SECOND * 60, 100);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		GPSUtilNew.stop(this);
	}
	
	protected void createNotificationIcon() {
//		String caption = (String) getText(R.string.app_name);
//		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//		Notification notification = new Notification(R.drawable.pictograms, caption , 0);
//		notification.flags |= Notification.FLAG_NO_CLEAR;
//		notification.flags |= Notification.FLAG_ONGOING_EVENT;
//
//		Intent intent = new Intent(this, ManagerNew.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        notification.setLatestEventInfo(this,  caption,
//                       getString(R.string.select_for_open), contentIntent);
//
//		notificationManager.notify(NOTIFICATION, notification);
	}
}
