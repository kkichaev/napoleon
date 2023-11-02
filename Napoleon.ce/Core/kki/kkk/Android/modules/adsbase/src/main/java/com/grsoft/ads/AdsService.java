package com.grsoft.ads;

import com.grsoft.ads.database.NoteHitching;
import com.grsoft.ads.database.PicStoreHitching;
import com.grsoft.ads.database.TaskAnswerHitching;
import com.grsoft.ads.database.TaskAttachmentInfoHitching;
import com.grsoft.ads.database.TaskHitching;
import com.grsoft.ads.database.TaskVisitHitching;
import com.grsoft.ads.dataobjects.Cagent;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitchingNew;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.dataobjects.impl.MessageNewImpl;
import com.grsoft.napoleon.dataobjects.impl.TaskImpl;
import com.grsoft.network.UpdateProcess;
import com.grsoft.network.UpdateProcess.Params;
import com.grsoft.util.PhoneStatusLogger;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;

public class AdsService extends Service {
	private IBinder binder = new AdsServiceBinder();
	private UpdateProcess updater;
	public static Class<? extends Service> MAIN_SERVICE = AdsService.class;
	private boolean synInProcess = false;
	
	public static final String SYNC_FINISHED = "com.grsoft.ads.AdsService.SYNC_FINISHED";
	public static final String SYNC_STARTED = "com.grsoft.ads.AdsService.SYN_STARTED";
	public static final String SYNC_RESULT = "com.grsoft.ads.AdsService.SYN_RESULT";
	public static final String SYNC_PROCESS_MODE = "com.grsoft.ads.AdsService.SYNC_PROCESS_MODE";
	public static final String OPEN_NEW_TASK_LIST = "com.grsoft.ads.AdsService.OPEN_NEW_TASK_LIST";
	public static final String SYNC_ACTION =  "com.grsoft.ads.AdsService.SYNC_ACTION";
	
	private SyncTimer syncbkgprocess;
	private CheckMissedTimer checkMissedTimer;
	private PowerManager.WakeLock wakeLock;
	private BroadcastReceiver phoneStatusLogger = new PhoneStatusLogger();
	
	public class AdsServiceBinder extends Binder{
		public AdsService getService(){ return AdsService.this;	}
	}
	
	@Override
	public IBinder onBind(Intent arg0) { return binder; }

	@Override
	public void onCreate() {
		super.onCreate();
		
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
		wakeLock.acquire();
		
		initgps();
		restartSyncBkg();
		restartTaskMissed();
		
		registerReceiver(startsync, new IntentFilter(SYNC_ACTION));
		registerReceiver(syncfinished, new IntentFilter(SYNC_FINISHED));
		registerReceiver(phoneStatusLogger, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		adsMsgNotify();
		
		LogImpl.log(com.grsoft.dataobjects.Log.PROGRAMM_STARTED);
	}

	protected void initgps() {
		try{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			int freq = Integer.valueOf(pref.getString(SettingGPS.GPSFREQ, SettingGPS.FREQ_DEF)) * 1000;
			int dist = Integer.valueOf(pref.getString(SettingGPS.GPSDIST, SettingGPS.DIST_DEF));
			GPSUtilNew.start(this, freq, dist);
		}catch(Exception e){ e.printStackTrace(); }
	}
	
	private BroadcastReceiver startsync = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Params params = new Params();
			
			params.outdata.add(new TaskAnswerHitching());
			params.outdata.add(new TaskVisitHitching());
			params.outdata.add(new NoteHitching());
			params.outdata.add(new GPSHitching());
			params.outdata.add(new LogHitching());
			
			params.slicedata.add(new PicStoreHitching());
			
			params.indata.add(createTaskHitching(context));
			params.indata.add(new Hitching(Org.class));
			params.indata.add(new Hitching(Cagent.class));
			params.indata.add(new TaskAttachmentInfoHitching());
			params.rcvdata.add(new MessageHitchingNew());
			
			setUserInfo(params);
			sync(params, true);
		}
	};

	public Hitching createTaskHitching(Context context){
		return  new TaskHitching(context);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		GPSUtilNew.stop(this);
		unregisterReceiver(startsync);
		unregisterReceiver(syncfinished);
		unregisterReceiver(phoneStatusLogger);
		
		if(syncbkgprocess != null)
			syncbkgprocess.cancel();
		
		if(checkMissedTimer != null)
			checkMissedTimer.cancel();
		
		if (wakeLock != null)
			wakeLock.release();
		
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(R.id.adsmsg);
		
		LogImpl.log(com.grsoft.dataobjects.Log.PROGRAMM_STOPPED);
		System.exit(0);
	}
	
	protected void setUserInfo(Params p) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String login = pref.getString(SettingNew.LOGIN, "");
		String pass = pref.getString(SettingNew.PASSWORD, "");
		String ip1 = pref.getString(SettingNew.IP1, "");
		String ip2 = pref.getString(SettingNew.IP2, "");
		
		final int DEFAULT_PORT = 8888;
		int port1 = DEFAULT_PORT;
		int port2 = DEFAULT_PORT;
		
		try{
			port1 = Integer.parseInt(pref.getString(SettingNew.PORT, ""));
			port2 = port1;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		p.ip1 = ip1;
		p.ip2 = ip2;
		p.port1 = port1;
		p.port2 = port2;
		p.login = login;
		p.pass = pass;
		
		/*---------------------*/
//		p.ip1 = "192.168.0.100";
//		p.port1 = 8888;
//		p.login = "2";
//		p.pass = "2";
	}

	public void sync(UpdateProcess.Params param, final boolean background){
		updater = new UpdateProcess(this){
			@Override protected void onPreExecute() { 
				synInProcess = true;
				Intent intent = new Intent(SYNC_STARTED);
				intent.putExtra(SYNC_PROCESS_MODE, background);
				sendBroadcast(intent);
			}
			
			@Override protected void onPostExecute(Boolean result) { 
				synInProcess = false;
				Intent intent = new Intent(SYNC_FINISHED);
				intent.putExtra(SYNC_RESULT, result);
				intent.putExtra(SYNC_PROCESS_MODE, background);
				sendBroadcast(intent);
			}
		};
		updater.execute(param);
	}

	public void syncterminate() {
		if (synInProcess && updater != null)
			updater.cancel(true);
	}
	
	public void restartSyncBkg(){
		if (syncbkgprocess != null)
			syncbkgprocess.cancel();
		
		long d = getTimeDelay(SettingSyncBkg.SYNCBKGSTEP, "5");
		
		if(d > 0 )
			syncbkgprocess = new SyncTimer(this, d);
	}
	
	private long getTimeDelay(String name, String def){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String v = pref.getString(name, def);
		
		long result = 0;
		
		try{
			result = Long.parseLong(v);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result * 60 * 1000;
	}
	
	protected void newTaskNotify() {
	}
	
	BroadcastReceiver syncfinished = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (TaskImpl.rcvdNewTasks()) 
				newTaskNotify();
			
			if (new MessageNewImpl().hasUnread())
				unreadMsgNotify();
		}
	};

	public void restartTaskMissed() {
		if (checkMissedTimer != null)
			checkMissedTimer.cancel();
		
		long d = getTimeDelay(SettingNotify.TASKMISSEDTIME, "2");
		
		if(d > 0 )
			checkMissedTimer = new CheckMissedTimer(this, d);
		
	}

	protected void unreadMsgNotify() {
		Intent a = new Intent(this, MessageList.class); 
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, a, 0);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String sound = pref.getString(SettingNotify.NEWTASKRCV, "content://settings/system/notification_sound");
		
		
		Notification.Builder builder = new Notification.Builder(this)
				.setContentText(this.getString(R.string.newmessage))
				.setSmallIcon(R.drawable.ic_insert_drive_file)
				.setAutoCancel(true)
				.setContentIntent(contentIntent)
				.setSound(Uri.parse(sound));
		
		
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		NotificationChannel channel = null;
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			channel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                DEFAULT_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
			nm.createNotificationChannel(channel);
			builder.setChannelId(DEFAULT_CHANNEL_ID);
		}
      
		Notification noti = null;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			noti = builder.getNotification();
		else
			noti = builder.build();
		
		nm.notify(R.id.unreadmsg, noti);
	}
	
	public final static String DEFAULT_CHANNEL_ID = "default_channel";

	public void restartGPS() {
		GPSUtilNew.stop(this);
		initgps();
	}
	
	protected void adsMsgNotify() {
		Intent a = new Intent(this, AdsNew.class); 
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, a, 0);
		
		Notification.Builder builder = new Notification.Builder(this)
				.setContentText(this.getString(R.string.notify_msg))
				.setSmallIcon(R.drawable.ic_h)
				.setAutoCancel(false)
				.setContentIntent(contentIntent)
				.setOngoing(true);
		
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		NotificationChannel channel = null;
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			channel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                DEFAULT_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
			nm.createNotificationChannel(channel);
			builder.setChannelId(DEFAULT_CHANNEL_ID);
		}
		
		Notification noti = null;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			noti = builder.getNotification();
		else
			noti = builder.build();

		startForeground(R.id.adsmsg, noti);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_STICKY;
	}
}
