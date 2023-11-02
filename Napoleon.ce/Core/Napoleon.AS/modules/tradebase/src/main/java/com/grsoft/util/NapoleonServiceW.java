package com.grsoft.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.database.GPSHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.RemnantsHitching;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.LogHitching;
import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.Messages;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkBroadcasts;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.exception.LoginFailure;
import com.grsoft.network.exception.RuntimeException;
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
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class NapoleonServiceW extends Service{
	private static final String NONE = "none";
	public final static String GPS_CHECK_STATUS_DISABLE = "manage_gps";
	public static final String SERVER_INTENT = "serverIntent";
	
	public final static String KEY_VAL = "Tracking";
	private static final String TAG = "NapoleonService";
	private static boolean isUpdateProcessActive;
	private int NOTIFICATION = R.string.app_name;
	protected DataSendScheduleSender dataSendScheduleSender;
	protected PriceUpdateTimer priceUpdateTimer;
	private final IBinder binder = new LocalBinder();
	private BroadcastReceiver phoneStatusLogger = new PhoneStatusLogger();
	private PowerManager.WakeLock wakeLock;
	
	public static List<Hitching> priceUpdateHitchings = new ArrayList<Hitching>();
	
	public class LocalBinder extends Binder {
		public NapoleonServiceW getService() {
			return NapoleonServiceW.this;
	    }
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	public final static String DEFAULT_CHANNEL_ID = "default_channel";

	@Override
	public void onCreate() {
		super.onCreate();

		GlobalServiceContext.service = this;
		Log.d(TAG, "service creating");
		
		if(isTracking())
			GPSUtilNew.start(this);
		
		CfgNplW config = (CfgNplW)ConfigManager.getConfig();
		
		if(config.dataSendInBackground)
			dataSendScheduleSender = new DataSendScheduleSender();
		
		initUpdatePrice(config);
		
		phoneStatusLogger = new PhoneStatusLogger();
		registerReceiver(phoneStatusLogger, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		String userid = ConfigManager.getConfig().impersonate;
		String agentName = "";
		
		if(userid.length() > 0){
			Map<String, Agent> agents = AgentInfoHelper.getAgents();
			
			if(agents.size() > 0 && agents.containsKey(userid)){
				agentName = agents.get(userid).name;
			}
		}
		
		String caption = (String) getText(R.string.app_name);
		
		if(agentName.trim().length() > 0)
			caption += " (" + agentName + ")";

		showNotify(caption);

		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NapoleonService:wakelock");
		wakeLock.acquire();

		LogImpl.log(com.grsoft.dataobjects.Log.PROGRAMM_STARTED);
		FLog.d("Napoleon starter");
		registerReceiver(new SystemActionReciever(), new IntentFilter("android.intent.action.TIME_TICK"));
		Log.d(TAG, "service created");
	}

	private void showNotify(String caption) {
		Intent intent = new Intent(this, RuntimeEnv.getMainActivity(this));
		intent.putExtra(SERVER_INTENT, true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

		Notification.Builder builder = new Notification.Builder(this)
				.setContentTitle(caption)
				.setContentText(getString(R.string.select_for_open))
				.setSmallIcon(R.drawable.napoleon)
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

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
			nm.notify(NOTIFICATION, noti);
		else
			startForeground(NOTIFICATION, noti);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(Consts.D_TAG, "Service destroyed");

		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION);
		
		if(dataSendScheduleSender != null)
			dataSendScheduleSender.cancel();

		if(priceUpdateTimer != null)
			priceUpdateTimer.cancel();
		
		GPSUtilNew.stop(this);
		unregisterReceiver(phoneStatusLogger);
		
		if (wakeLock != null)
			wakeLock.release();

		LogImpl.log(com.grsoft.dataobjects.Log.PROGRAMM_STOPPED);
		FLog.d("Napoleon stopped");
		
		System.exit(0);
	}
	
	protected LoginData getUserInfo(CfgNplW config) {
		return new LoginData(config.login, config.passw, config.impersonate,  NapoleonServiceW.this
			,config.uuid, config.serverCode);
	}
	
	
	public void showMessageOnNotifyPanel(){
		Message[] message = MessageStock.getNewMessage();
		
		for(int i = 0; i < message.length; i++)
				makeMsgNotification(message[i].date, message[i].message);
	}
	
	private void makeMsgNotification(Date data, String text){
		Intent intent = new Intent(this, Messages.class);
		intent.putExtra(SERVER_INTENT, true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

		Notification.Builder builder = new Notification.Builder(this)
			.setContentTitle(getString(R.string.message))
         	.setContentText(text)
         	.setSmallIcon(R.drawable.message)
         	.setAutoCancel(true)
         .	setContentIntent(contentIntent);

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

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
			nm.notify(NOTIFICATION, noti);
		else
			startForeground(NOTIFICATION, noti);
	}
	
	public void update(){
		if (dataSendScheduleSender != null)
			dataSendScheduleSender.cancel();
		
		CfgNplW cfg = (CfgNplW)ConfigManager.getConfig();
		if(cfg.dataSendInBackground && cfg.impersonate.trim().length() == 0)
			dataSendScheduleSender = new DataSendScheduleSender();
		
		if(priceUpdateTimer != null)
			priceUpdateTimer.cancel();
		
		initUpdatePrice(cfg);
	}

	protected void initUpdatePrice(CfgNplW cfg) {
		if(Features.UPDATE_PRICE_BACKGROUND && cfg.useUpdatePrice)
			priceUpdateTimer = new PriceUpdateTimer();
	}
	
	public class PriceUpdateTimer extends Timer {
		public PriceUpdateTimer(){
			final int DELAY_TIME = getDelayTime();
			scheduleAtFixedRate(new PriceUpdateTask(), DELAY_TIME, DELAY_TIME);
			Log.d(getClass().getCanonicalName(), String.format("delay timer =%d", DELAY_TIME));
		}
		
		protected int getDelayTime(){
			return ((CfgNplW)ConfigManager.getConfig()).updatePriceTime * Consts.ONE_SECOND * Consts.SEC_PER_MIN;
		}
		
		class PriceUpdateTask extends TimerTask{
			@Override
			public void run() {
				try{
					Log.d(getClass().getCanonicalName(), "start update price background");
					LogImpl.log(com.grsoft.dataobjects.Log.BKG_PRICE);
					List<Hitching> rcv = new ArrayList<Hitching>();
					rcv.add(new RemnantsHitching());
					rcv.addAll(priceUpdateHitchings);
					
//					if(Features.DDLV)
//						rcv.add(new DayDeliveryHitching());
					
					CfgNplW config = (CfgNplW) ConfigManager.getConfig();
					
					if(config == null)
						return;
					
					UserInfo userInfo = getUserInfo(config);
					ReadServiceBase rs = RWServiceFactory.instance.createReadService(rcv);
					rs.update(getApplicationContext(), userInfo, false);
					NetworkBroadcasts.sendSyncResult(NapoleonServiceW.this, true);
					Log.d(getClass().getCanonicalName(), "end update price background");
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				try{
					showMessageOnNotifyPanel();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	class DataSendScheduleSender extends Timer{
		private final String TAG ="GpsScheduleSender";
		
		public DataSendScheduleSender(){
			final int DELAY_TIME = 
				((CfgNplW)ConfigManager.getConfig()).gpsSendInterval * 
				Consts.ONE_SECOND * Consts.SEC_PER_MIN;
			scheduleAtFixedRate(new GPSSenderTask(), DELAY_TIME, DELAY_TIME);
			Log.d(TAG, String.format("delay timer =%d", DELAY_TIME));
		}
		
		class GPSSenderTask extends TimerTask{
			private final String TAG = "GPSSenderTask"; 
			@Override
			public void run() {
				if(!isUpdateProcessActive){
					try{
						Log.d(TAG, "background send begin");
						LogImpl.log(com.grsoft.dataobjects.Log.BKG_SYNC);
						List<ObjectListener> docs = new ArrayList<ObjectListener>();
						GPSHitching gps = new GPSHitching();
						
						if( gps.size() > 0 )
							docs.add(gps);
						
						LogHitching logHitching = new LogHitching();
						
						if (logHitching.needUpdate())
							docs.add(logHitching);
						
						sendData(docs);
						showMessageOnNotifyPanel();
						
						Log.d(TAG, "background send end");
					}catch (Exception e) {
						Log.d(TAG, "send was broken");
						e.printStackTrace();
					}
				}
			}
			
		}
		
		
		protected void sendData(List<? extends ObjectListener> cursorListener) 
			throws RuntimeException, LoginFailure
		{
			CfgNplW config = (CfgNplW) ConfigManager.getConfig();
			
			if(config == null)
				return;
			
			UserInfo userInfo = getUserInfo(config);
			
			WriteServiceBase writeService = RWServiceFactory.instance
					.createWriteService(cursorListener);
			configWriteSetvice(writeService);
			writeService.write(NapoleonServiceW.this, userInfo);
		}
	}

	public static boolean isTracking(){
		Boolean gpsCheckStatusDisable =
				(Boolean)ConfigManager.getConfig().getProperty(GPS_CHECK_STATUS_DISABLE);
		
		boolean result = false;
		
		if (gpsCheckStatusDisable != null && gpsCheckStatusDisable == true)
			result = true;
		else if (!((CfgNplW)ConfigManager.getConfig()).loggable)
			result = false;
		else{
			ConfigImpl configImpl = new ConfigImpl();
			configImpl.getData().key = KEY_VAL;
			configImpl.checkDBTable();
			if (configImpl.read() && !configImpl.getData().value.equals(NONE))
					result = true;
			
			configImpl.close();
		}
		
		return result;
	}
	
	public void configWriteSetvice(WriteServiceBase writeService) {
	}

	public static void setUpdatePocessActive(boolean val) {
		isUpdateProcessActive = val;
	}
}
