package com.grsoft.napoleon;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.DatePeriod;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;


public class DocSendWatchService extends Service {
	private Timer timer;
	private long DELAY = 60 * 1000 * 1; 
	private final static String HAS_NOT_SENDED_DOCS_ACTION = "com.grsoft.napoleon.DocSendWatchService.HAS_NOT_SENDED_DOCS_ACTION";
		
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		timer = new Timer();
		timer.schedule(timerTask, DELAY, DELAY);
		registerReceiver(rcv, new IntentFilter(HAS_NOT_SENDED_DOCS_ACTION));
	}
	
	TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			if (hasNotSendedDocs(getInvalidTime()))
				sendNotSendAction();
		}

		protected void sendNotSendAction() {
			Intent i = new Intent(HAS_NOT_SENDED_DOCS_ACTION);
			sendBroadcast(i);
		}

		protected int getInvalidTime() {
			int result = 0;
			final String key = "ЗаяквиДолжныОтправлены";
			StringBuilder sb = new StringBuilder();
			ConfigImpl cfg = new ConfigImpl();
			
			if (cfg.getValue(sb, key)){
				try{
					result = Integer.parseInt(sb.toString());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			return result;
		}
		
		private boolean hasNotSendedDocs(int min){
			boolean result = false;
			
			Order ord = getNotSendedOrder();
			
			if(min > 0 && ord != null)
				result = DatePeriod.minDiff(ord.created, new Date()) >= min;
			
			return result;
		}
		
		private Order getNotSendedOrder(){
			Order result = null;
			
			DbWriter.checkDBTable(Order.class);
			String where = "(([params] & 1 ) == 0)";
			
			List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Order.class), where, "created desc");
			
			if(ids.size() > 0){
				OrderImpl ord = new OrderImpl();
				
				if (ord.read(ids.get(0)))
					result = ord.getData();
				
				ord.close();
			}
			
			return result;
		}
	};
	
	BroadcastReceiver rcv = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent a = new Intent(context, UpdateDBEx.class); 
	        TaskStackBuilder stack = TaskStackBuilder.create(context);
	        stack.addParentStack(UpdateDBEx.class);
	        stack.addNextIntent(a);
	        PendingIntent ci = stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	        
			NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			Notification.Builder b = new Notification.Builder(context)
		        .setSmallIcon(R.drawable.task_alert)
		        .setContentIntent(ci)
		        .setContentTitle(getString(R.string.app_name))
		        .setAutoCancel(true)
		        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
		        .setContentText(getString(R.string.has_notsend_docs));
			nm.notify(R.id.unsended_docs_notify_id, b.getNotification());
		}
	};
}
