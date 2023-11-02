package com.grsoft.chat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.grsoft.database.ChatRcvHitching;
import com.grsoft.database.ChatSndHitching;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.ChatAgent;
import com.grsoft.network.ActionTimer;
import com.grsoft.network.UpdateProcess;
import com.grsoft.network.UpdateProcess.Params;

public class ChatService extends Service{
	public static final String SYNC_ACTION = "com.grsoft.chat.ChatService.SYNC_ACTION";
	public static final String SYNC_FINISHED = "com.grsoft.chat.ChatService.SYNC_FINISHED";
	public static final String SYNC_RESULT = "com.grsoft.chat.ChatService.SYNC_RESULT";
	
	protected ActionTimer refreshTimer;
	
	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	public void sync(Params param, final boolean background){
		UpdateProcess updater = new UpdateProcess(this){
			@Override protected void onPostExecute(Boolean result) { 
				Intent intent = new Intent(SYNC_FINISHED);
				intent.putExtra(SYNC_RESULT, result);
				sendBroadcast(intent);
			}
		};
		
		updater.execute(param);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(refreshTimer != null)
			refreshTimer.cancel();
		
		unregisterReceiver(updateBroadcast);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		refreshTimer = new ActionTimer(this, getRefreshDelay(), SYNC_ACTION);
		registerReceiver(updateBroadcast, new IntentFilter(SYNC_ACTION));
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		createNotify();
		return super.onStartCommand(intent, flags, startId);
	}

	private long getRefreshDelay() {
		final int DEFAULT_DEL_TIME = 1000 * 10;  
		return DEFAULT_DEL_TIME;
	}
	
	BroadcastReceiver updateBroadcast = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Params p = new Params();
			setUserInfo(p);
			inputHitching(p);
			outputHitching(p);
			sync(p, true);
		}

		
	};
	
	protected void inputHitching(Params p) {
		p.indata.add(new ChatRcvHitching());
		p.indata.add(new Hitching(ChatAgent.class));
	}

	protected void setUserInfo(Params p) {}
	
	protected void createNotify(){
		Context ctx = getApplicationContext();
		Intent a = new Intent(this, Chat.activity); 
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, a, 0);
        
        NotificationCompat.Builder bld = new NotificationCompat.Builder(ctx);
        bld.setContentText(this.getString(R.string.chat_title));
        bld.setSmallIcon(R.drawable.chat);
        bld.setAutoCancel(false);
        bld.setContentIntent(contentIntent);
        
        Notification noti = bld.build();
        noti.flags |= Notification.FLAG_NO_CLEAR;
        
        bld.setContentIntent(contentIntent);
        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        
        nm.notify(R.id.chat, noti);
        
//        Notification noti = new Notification.Builder(this)
//         .setContentText(this.getString(R.string.chat_title))
//         .setSmallIcon(R.drawable.chat)
//         .setAutoCancel(false)
//         .setContentIntent(contentIntent)
//         .build();
//		
//        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//		nm.notify(R.id.chat, noti);
	}

	protected void outputHitching(Params p) {
		p.outdata.add(new ChatSndHitching());
	}
}
