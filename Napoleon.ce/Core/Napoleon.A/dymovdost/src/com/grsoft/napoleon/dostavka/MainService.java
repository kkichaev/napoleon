package com.grsoft.napoleon.dostavka;

import java.util.Timer;
import java.util.TimerTask;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.documents.DispatchDoc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.UpdateProcess;
import com.grsoft.network.UpdateProcess.Params;
import com.grsoft.util.Consts;
import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.gps.GPSUtilNew;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;


public class MainService extends Service {
	private Timer timer;
	private static final int TIMER_DELAY = 5000 * 60;
	public static final String SYNC_FINISHED = "com.grsoft.napoleon.ExchangeService.SYNC_FINISHED";
	public static final String SYNC_RESULT = "com.grsoft.napoleon.ExchangeService.SYNC_RESULT";
	public static final String SYNC_ACTION = "com.grsoft.chat.ExchangeService.SYNC_ACTION";
	public static final String CLEAR_BASE = "clear_base";
	private final IBinder binder = new LocalBinder();
	private NotificationManager  notificationManager; 
	private PowerManager.WakeLock wakeLock;
	
	@Override public IBinder onBind(Intent intent) { return binder; }
	
	@Override
	public void onCreate() {
		super.onCreate();
		initRcvTimer();
		initToolbarIcon();
		
		if(!RoutePointImpl.isRouteComplete())
			GPSUtilNew.start(this);
	}

	protected void initRcvTimer() {
		int delay = getTimerDelay();
		timer = new Timer();
		timer.schedule(new TimerTask() { @Override	public void run() { timetTaskFunc(); }}, delay, delay);
		registerReceiver(timerrcv, new IntentFilter(SYNC_ACTION));
	}
	
	private void timetTaskFunc(){
		LogImpl.log(com.grsoft.dataobjects.Log.BKG_SYNC);
		Intent i = new Intent(SYNC_ACTION);
		i.putExtra(CLEAR_BASE, false);
		sendBroadcast(i);
	}

	protected int getTimerDelay() {
		int delay = TIMER_DELAY;
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		try{
			delay = Integer.parseInt(pref.getString(getString(R.string.sync_time_pref), getString(R.string.sync_time_def_val)));
			delay *= Consts.ONE_SECOND * Consts.SEC_PER_MIN;
		}catch(Exception e){
			e.printStackTrace();
		}
		return delay;
	}
	
	public void restartTimer(){
		int delay = getTimerDelay();
		timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() { @Override	public void run() { timetTaskFunc(); }}, delay, delay);
	}
	
	private void initToolbarIcon() {
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.pictograms,  getString(R.string.app_name) , 0);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		Intent intent = new Intent(this, RuntimeEnv.getMainActivity(this)); 
		
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notification.setLatestEventInfo(this,  getString(R.string.app_name),
                       getString(R.string.select_for_open), contentIntent);
		
		notificationManager.notify(R.id.app_notify, notification);
		
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
		wakeLock.acquire();
	}

	@Override
	public void onDestroy() {
		notificationManager.cancel(R.id.app_notify);
		unregisterReceiver(timerrcv);
		
		if (wakeLock != null)
			wakeLock.release();
		
		super.onDestroy();
	};
	
	BroadcastReceiver timerrcv = new BroadcastReceiver(){ 
		@Override public void onReceive(Context context, Intent intent) {
			boolean clear = intent.getBooleanExtra(CLEAR_BASE, false);
			sync(clear);	
		} };
	
	private void cleardata() {
		Path.clearDataDir();
		DataBaseManager.clearBase();
	}

	private synchronized void sync(final boolean clear) {
		UpdateProcess upd = new UpdateProcess(this){
			@Override protected void onPostExecute(Boolean result) { 
				Intent intent = new Intent(SYNC_FINISHED);
				intent.putExtra(SYNC_RESULT, result);
				sendBroadcast(intent);
				
				if(result)
					initForNewSetting();
				
				if(!RoutePointImpl.isRouteComplete())
					GPSUtilNew.start(MainService.this);
			}
			
			@Override protected void sendComplete() { if(clear) cleardata(); }
		};
				
		Params p = new Params();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		p.ip1 = pref.getString(getString(R.string.ip1_pref), getString(R.string.def_address));
		p.ip2 = pref.getString(getString(R.string.ip2_pref), "");
		
		p.port1 =  Integer.parseInt(pref.getString(getString(R.string.port_pref),getString(R.string.def_port_val)));
		String key = pref.getString(NapoleonApp.AUTORIZATION_KEY, "").trim();
		p.login = key;
		p.pass = key;
		
		p.indata.add(new Hitching(RoutePoint.class));
		
		DocExportListener rd = DispatchDoc.instance().getDirtyDocuments();
		
		if(rd != null)
			p.outdata.add(rd);
		
		if(rd != null)
			p.outdata.add(rd);
		
		p.outdata.add(new GPSHitching());
		p.outdata.add(new LogHitching());
		
		upd.execute(p);
	}
	
	protected void initForNewSetting() {
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		final String GPS_DIST = "GpsDist";
		final String GPS_FREQ = "GpsFreq";
		final String SYNC_TIME = "SyncTime";
		
		Config config = ConfigManager.getConfig();
		boolean edit = false;
		if (cfg.getValue(sb, GPS_DIST)){
			try{
				config.gpsDistance = Integer.parseInt(sb.toString().trim());
				edit = true;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		sb.setLength(0);
		if (cfg.getValue(sb, GPS_FREQ)){
			try{
				config.gpsFrequience = Integer.parseInt(sb.toString().trim()) *  Consts.ONE_SECOND;
				edit = true;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
			
		if (edit)
			ConfigManager.save();
		
		sb.setLength(0);
		if(cfg.getValue(sb, SYNC_TIME)){
			try{
				int min = Integer.parseInt(sb.toString().trim());
				int msec = min * Consts.ONE_SECOND * Consts.SEC_PER_MIN;
				if (msec != getTimerDelay()){
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
					Editor ed = pref.edit();
					ed.putString(getString(R.string.sync_time_pref), Integer.toString(min));
					ed.commit();
					restartTimer();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public class LocalBinder extends Binder { public MainService getService() { return MainService.this; }	}
}
