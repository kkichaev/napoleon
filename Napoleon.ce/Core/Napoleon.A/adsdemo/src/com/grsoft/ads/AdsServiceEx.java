package com.grsoft.ads;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.impl.OrderImplEx;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.ads.R;
import com.grsoft.util.Consts;
import com.grsoft.util.GlobalServiceContext;
import com.grsoft.util.RuntimeEnv;

public class AdsServiceEx extends AdsService {
	private OrderCheckTimer orderCheckTimer;

	@Override
	public void onCreate() {
		super.onCreate();
		
		orderCheckTimer = new OrderCheckTimer();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (orderCheckTimer != null)
			orderCheckTimer.cancel();
	}
	
	class OrderCheckTimer extends Timer{
		private final int DELAY_TIME = Consts.ONE_SECOND * Consts.SEC_PER_MIN;
		
		public OrderCheckTimer(){
			scheduleAtFixedRate(new OrderCheckTimerTask(), DELAY_TIME, DELAY_TIME);
		}
		
		class OrderCheckTimerTask extends TimerTask{

			private static final int MISSED_NOTIFY = 1024;

			@Override
			public void run() {
				SharedPreferences pref = AdsServiceEx.this.getSharedPreferences(
						Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
				int interval = Integer.parseInt(pref.getString("interval_missed", "0"));
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -interval);
				
				SQLiteDatabase db = DataBaseManager.getDataBase();
				Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Order.class), 
						new String[]{"rowid"}, "planbegin < ? and params=0", 
						new String[]{Long.toString(cal.getTime().getTime())}, null, null, null);
				
				OrderImplEx orderImpl = new OrderImplEx();
				boolean hasMissed = false;
				
				while (c.moveToNext()){
					if (!hasMissed)
						hasMissed = true;
					
					if (orderImpl.read(c.getLong(0), false)){
						orderImpl.setMissed();
						orderImpl.write();
						orderImpl.close();
					}
				}
				
				c.close();
				
				if (hasMissed){
					Notification notification = new Notification(R.drawable.kalarm, "Наполеон АДС" , 0);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					
					Intent intent = new Intent(AdsServiceEx.this, RuntimeEnv.getMainActivity(AdsServiceEx.this)); 
			        PendingIntent contentIntent = PendingIntent.getActivity(AdsServiceEx.this, 0, intent, 0);

			        notification.setLatestEventInfo(AdsServiceEx.this, "АДС",
			                       "Время начала выполнения заявки истекло!.", contentIntent);
					
					String message_snd = pref.getString("order_missed", "");
					
					notification.sound = Uri.parse(message_snd);
					
					notificationManager.notify(MISSED_NOTIFY, notification);
					
					GlobalServiceContext.service.sendBroadcast(new Intent(AdsEx.UPDATE_LIST_ACTION));
				}
			}
		}
	}
}
