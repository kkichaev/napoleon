package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.PrezentHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RouteHitching;
import com.grsoft.database.RouteItemHitching;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.OrgLocation;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Route;
import com.grsoft.dataobjects.RouteItem;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.UpdateProcess;
import com.grsoft.network.UpdateProcess.Params;
import com.grsoft.util.Consts;
import com.grsoft.util.PhoneStatusLogger;
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
import android.net.Uri;
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
	private final IBinder binder = new LocalBinder();
	private NotificationManager  notificationManager; 
	private PowerManager.WakeLock wakeLock;
	private static final String LOGIN = "login";
	private BroadcastReceiver phoneStatusLogger = new PhoneStatusLogger();
	List<ObjectListener> addToSend;
	public static List<Hitching> AddRequest = new ArrayList<Hitching>(); 
	
	@Override public IBinder onBind(Intent intent) { return binder; }
	
	@Override
	public void onCreate() {
		super.onCreate();
		initRcvTimer();
		initToolbarIcon();
		
		//MichelChat.init(this);
		GPSUtilNew.start(this);
		
		phoneStatusLogger = new PhoneStatusLogger();
		registerReceiver(phoneStatusLogger, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	protected void initRcvTimer() {
		int delay = getTimerDelay();
		timer = new Timer();
		timer.schedule(new TimerTask() { @Override	public void run() { timetTaskFunc(); }}, delay, delay);
		registerReceiver(timerrcv, new IntentFilter(SYNC_ACTION));
	}
	
	private void timetTaskFunc(){
		LogImpl.log(com.grsoft.dataobjects.Log.BKG_SYNC);
		sendBroadcast(new Intent(SYNC_ACTION));
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
		
		unregisterReceiver(phoneStatusLogger);
		
		super.onDestroy();
	};
	
	public void addSendedData(List<ObjectListener> toSend) {
		addToSend = toSend;
	}
	
	BroadcastReceiver timerrcv = new BroadcastReceiver(){ @Override public void onReceive(Context context, Intent intent) { sync(false);	} };
	
	private void cleardata() {
		Path.clearDataDir();
		DataBaseManager.clearBase();
	}

	public synchronized void sync(final boolean clear) {
		sync(clear, createParams(), false);
	}
	
	public synchronized void recieve(List<Hitching> rcv) {
		Params p = makeSyncParams();
		p.indata.addAll(rcv);
		sync(false, p, false);
	}
	
	public synchronized void send(List<ObjectListener> toSend, boolean doSilent) {
		Params p = makeSyncParams();
		p.outdata.addAll(toSend);
		sync(false, p, doSilent);
	}
	
	public boolean isLoginChanged() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String curLogin = pref.getString(getString(R.string.login_pref), "");
		String prevLogin = pref.getString(LOGIN, "");
		
		return !curLogin.equals(prevLogin);
	}
	
	protected Params makeSyncParams() {
		Params p = new Params();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		p.ip1 = pref.getString(getString(R.string.ip1_pref), "");
		p.ip2 = pref.getString(getString(R.string.ip2_pref), "");
		p.port1 =  Integer.parseInt(pref.getString(getString(R.string.port_pref),getString(R.string.def_port_val)));
		p.login = pref.getString(getString(R.string.login_pref), "");
		p.pass = pref.getString(getString(R.string.pass_pref), "");
		return p;
	}

	protected Params createParams() {
		Params p = makeSyncParams();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String prev = pref.getString(LOGIN, "");
		
//		if(BuildConfig.DEBUG){
//			p.ip1 = "192.168.0.100";
//			p.login = "2";
//			p.pass = "2";
//		}
		
		List<DocExportListener> output = DocTypeBase.getDocuments(true, !Features.UNLIMIT_VISIT_ITEMS);
		
		if(output != null && output.size() > 0)
			p.outdata.addAll(output);
		
		p.sendPhotos = Features.UNLIMIT_VISIT_ITEMS;
		p.outdata.add(new GPSHitching());
		p.outdata.add(new LogHitching());
		if(addToSend != null)
			p.outdata.addAll(addToSend);
		
		if(!prev.equals(p.login)){
			p.indata.add(new RcvNewHitching(Route.class));
			p.indata.add(new RcvNewHitching(RouteItem.class));
		}else{
			p.indata.add(new RouteHitching());
			p.indata.add(new RouteItemHitching());
		}
		
		p.indata.add(new Hitching(RoutePoint.class));
		p.indata.add(new Hitching(Waybill.class));
		p.indata.add(new Hitching(Price.class, "DPrice"));
		p.indata.add(new Hitching(Config.class, "Config"));
		p.indata.add(new Hitching(Config.class, "ServerConfig"));
		p.indata.add(new RcvNewHitching(OrgLocation.class));
		p.indata.add(new PrezentHitching(this));
		
		for(Hitching h : AddRequest) {
			p.indata.add(h);
		}
		
		return p;
	}
	
	private void sync(final boolean clear, Params param, boolean doSilent){
		UpdateProcess upd = new UpdateProcess(this, doSilent){
			@Override protected void onPostExecute(Boolean result) { 
				saveSyncData(result);
				
				Intent intent = new Intent(SYNC_FINISHED);
				intent.putExtra(SYNC_RESULT, result);
				sendBroadcast(intent);
				
				GPSUtilNew.start(MainService.this);
				
				if(!result)
					showFailSync();
			}
			
			private void showFailSync() {
				String sound = "content://settings/system/notification_sound";
		        
		        Notification not = new Notification.Builder(getApplicationContext())
		         .setContentText(getString(R.string.sync_lost))
		         .setSmallIcon(R.drawable.pictograms)
		         .setSound(Uri.parse(sound))
		         .getNotification();
				
		        NotificationManager m = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
				m.notify(R.id.lost_sync_ntf, not);
			}

			@Override protected void sendComplete() { if(clear) cleardata(); }
		};
		
		upd.execute(param);
	}
	
	protected void saveSyncData(boolean result) {
		if(result) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			String login = pref.getString(getString(R.string.login_pref), "");
			Editor ed = pref.edit();
			ed.putString(LOGIN, login);
			ed.commit();
		}
	}

	public class LocalBinder extends Binder { public MainService getService() { return MainService.this; }	}
}
