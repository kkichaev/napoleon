package com.grsoft.ads;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.grsoft.ads.utils.ConfigReader;
import com.grsoft.ads.utils.LockOwner;
import com.grsoft.ads.utils.gps.WorkDayTracking;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.GlobalServiceContext;
import com.grsoft.util.MessageStock;
import com.grsoft.util.PhoneStatusLogger;
import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.gps.GPSUtilNew;

public class AdsService extends Service 
implements LockOwner {
	private static final String TAG = "AdsService";
	protected static final int CHECK_GPS_LISTENER = 0;
	protected NotificationManager  notificationManager; 
	private int NOTIFICATION = R.string.app_name;
	//private WatchdogGpsListeners watchdogGpsListeners;
	protected DataSendScheduleSender dataSendScheduleSender;
	private final IBinder binder = new LocalBinder();
	private final Lock lock = new ReentrantLock();
	private BroadcastReceiver phoneStatusLogger = new PhoneStatusLogger();

	public class LocalBinder extends Binder {
		public AdsService getService() {
			return AdsService.this;
	    }
	}
	 
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "service creating");
		GlobalServiceContext.service = this;
		updateDatabase();
		gpsInit();
		
		phoneStatusLogger = new PhoneStatusLogger();
		registerReceiver(phoneStatusLogger, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		if(((ConfigReader)ConfigManager.getConfig()).isDataSendInBackground())
			dataSendScheduleSender = new DataSendScheduleSender();
		
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, "Наполеон АДС" , 0);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		Intent intent = new Intent(this, RuntimeEnv.getMainActivity(this)); 
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notification.setLatestEventInfo(this, "АДС",
                       "Выберите, чтобы открыть Наполеон АДС.", contentIntent);
		
		notificationManager.notify(NOTIFICATION, notification);

		LogImpl.log(com.grsoft.dataobjects.Log.PROGRAMM_STARTED);
		Log.d(TAG, "service created");
	}
	

	private void updateDatabase(){
		SQLiteDatabase db = DataBaseManager.getDataBase();
		Cursor c = db.rawQuery("pragma table_info ('clients')", null);
		boolean needConversion = false;
		
		while(c.moveToNext()){
			if (c.getString(c.getColumnIndex("name")).equals("id") &&
					c.getString(c.getColumnIndex("type")).toUpperCase().equals("INTEGER"))
				needConversion = true;
		}
		
		c.close();
		
		if (needConversion){
			db.beginTransaction();
			try{
				db.execSQL("DROP TABLE IF EXISTS [clients_tmp]");
				db.execSQL("ALTER TABLE [clients] RENAME TO [clients_tmp]");
				db.execSQL("CREATE TABLE [clients] ('address' TEXT, 'contacts' BLOB," +
						"'id' TEXT,'name' TEXT, PRIMARY KEY (id))");
				db.execSQL("INSERT INTO [clients] ([address], [contacts], [name] ,[id]) " +
						"SELECT [address], [contacts], [name] ,[id] FROM [clients_tmp]");
				db.execSQL("DROP TABLE IF EXISTS [clients_tmp]");
				
				db.execSQL("DROP TABLE IF EXISTS [orders_tmp]");
				db.execSQL("ALTER TABLE [orders] RENAME TO [orders_tmp]");
				db.execSQL("CREATE TABLE [orders] ([address] TEXT,[client] " +
						"TEXT, [factbegin] INTEGER, [factend] INTEGER, [items] BLOB," +
						"[number] TEXT, [planbegin] INTEGER, [planend] INTEGER, [text] TEXT," +
						"[userid] TEXT, [created] INTEGER, [remark] TEXT, [podRemark] TEXT," +
						"[params] INTEGER, [longitude] INTEGER, [latitude] INTEGER," +
						"[date] INTEGER,[id] TEXT,PRIMARY KEY (created))");
				db.execSQL("INSERT OR REPLACE INTO [orders]([address], [userid], [factbegin]," +
						"[factend], [items], [number], [planbegin], [planend], [text], [client]," +
						"[created], [remark], [podRemark], [params], [longitude], [latitude]," +
						"[date], [id]) SELECT [address], [userid], [factbegin]," +
						"[factend], [items], [number], [planbegin], [planend], [text], [client]," +
						"[created], [remark], [podRemark], [params], [longitude], [latitude]," +
						"[date], [id] FROM [orders_tmp]");
				db.execSQL("DROP TABLE IF EXISTS [orders_tmp]");
				db.setTransactionSuccessful();	
			}finally{
				db.endTransaction();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(Consts.D_TAG, "Service destroyed");
		notificationManager.cancel(NOTIFICATION);
		
		//if(watchdogGpsListeners != null)
		//	watchdogGpsListeners.cancel();
		
		if(dataSendScheduleSender != null)
			dataSendScheduleSender.cancel();
		
		GPSUtilNew.stop(this);
		unregisterReceiver(phoneStatusLogger);
		LogImpl.log(com.grsoft.dataobjects.Log.PROGRAMM_STOPPED);
	}
	
//	class WatchdogGpsListeners extends Timer{
//		private final int DELAY_TIME = Consts.ONE_SECOND * 30;
//		
//		public WatchdogGpsListeners(){
//			scheduleAtFixedRate(new WGLTimerTask(), DELAY_TIME, DELAY_TIME);
//		}
//		
//		class WGLTimerTask extends TimerTask{
//
//			@Override
//			public void run() {
//				handler.sendEmptyMessage(CHECK_GPS_LISTENER);
//			}
//		}
//		
//	}
	
//	private Handler handler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			switch(msg.what){
//			case CHECK_GPS_LISTENER:
//				gpsUtil.checkGpsListeners();
//				break;
//			}
//		}
//	};
	
	public void update(){
		if (dataSendScheduleSender != null)
			dataSendScheduleSender.cancel();
		
		if(((ConfigReader)ConfigManager.getConfig()).isDataSendInBackground())
			dataSendScheduleSender = new DataSendScheduleSender();
	}
	
	class DataSendScheduleSender extends Timer{
		private final String TAG ="GpsScheduleSender";
		private int notificationID = 0;
		private UpdateProcess updateProcess;
		
		public DataSendScheduleSender(){
			final int DELAY_TIME = ((ConfigReader)ConfigManager.getConfig()).getDataSendInterval() * 
				Consts.ONE_SECOND * Consts.SEC_PER_MIN;
			scheduleAtFixedRate(new GPSSenderTask(), DELAY_TIME, DELAY_TIME);
			Log.d(TAG, String.format("delay timer =%d", DELAY_TIME));
			
			updateProcess = UpdateProcess.createProcess(AdsService.this, AdsService.this);
		}
		
		class GPSSenderTask extends TimerTask{
			private final String TAG = "GPSSenderTask"; 
			@Override
			public void run() {
				Log.d(TAG, "send begin");
	    		updateProcess.doInBackground((Void[])null);
	    		showMessageOnNotifyPanel();
	    		getSharedPreferences(
						Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).
							edit().putBoolean(Setting.CLEAR, false).commit();
	    		GlobalServiceContext.service.sendBroadcast(new Intent(Ads.UPDATE_ACTION));
			}
			
		}
		
		public void showMessageOnNotifyPanel(){
			Message[] message = MessageStock.getNewMessage();
			
			for(int i = 0; i < message.length; i++)
					makeMsgNotification(message[i].date, message[i].message);
		}
		
		private void makeMsgNotification(Date data, String text){
			Notification notification = new Notification(R.drawable.message, "Сообщение" , 0);
			
	        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, null, 0);
	        notification.setLatestEventInfo(getApplicationContext(), 
	        		"Наполеон АДС: Вам сообщение",text , contentIntent);
	        
	        SharedPreferences pref = getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE); 
	        String message_snd = pref.getString(Setting.MESSAGE_SND, "");
	        Log.d(TAG, message_snd);
			notification.sound = Uri.parse(message_snd);
			
			if (pref.getBoolean(Setting.VIBRATE, false))
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			
			notificationManager.notify(notificationID, notification);
		}
	}

	@Override
	public Lock getLock() {
		return lock;
	}
	
	public void gpsInit(){
		if (WorkDayTracking.isWorkingTime())//{
			GPSUtilNew.start(this);
			
//			if (watchdogGpsListeners == null)
//				watchdogGpsListeners = new WatchdogGpsListeners();
//		}else
//			GPSUtilNew.start(this);
	}
}
