package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.SyncInfoImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.LoginData;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.WriteService;
import com.grsoft.util.FLog;
import com.grsoft.util.Util;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendDocsService extends IntentService {
	private static final String TAG = SendDocsService.class.getCanonicalName();

	public SendDocsService() {
		super("SendDocsService");
	}
	
	public static void registerService(Context context) {
		FLog.d("SendDocsService.registerService start");
		ConfigImpl ci = new ConfigImpl();
		com.grsoft.dataobjects.Config c = ci.getData();
		c.key = "ФоноваяСинхронизация";
		if(ci.read()) {
			FLog.d("SendDocsService.registerService has config");
			String[] vals = c.value.split(":");
			if(vals.length == 2) {
				int h, m;
				h = Integer.parseInt(vals[0]);
				m = Integer.parseInt(vals[1]);
			
				FLog.d(String.format("SendDocsService.registerService register time %s:%s", vals[0], vals[1]));
				
				Calendar clnd = Calendar.getInstance(Locale.getDefault());
				int curh = clnd.get(Calendar.HOUR_OF_DAY); 
				if((h < curh) || (h == curh && m < clnd.get(Calendar.MINUTE)))
					clnd.add(Calendar.DAY_OF_MONTH, 1);
				
				clnd.set(Calendar.HOUR_OF_DAY, h);
				clnd.set(Calendar.MINUTE, m);
				
				Intent i = new Intent(context, SendDocsService.class);
				PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, clnd.getTimeInMillis(), 24 * 3600 * 1000l, pi);
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());
				Log.d(TAG, "Register send docs on " + sdf.format(clnd.getTime()));
			}
		}
		ci.close();
		FLog.d("SendDocsService.registerService finish");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.d(TAG, "Starting");
		FLog.d("SendDocsService.onHandleIntent start");
		
		List<DocExportListener> exportedDocs = DocType.getDocuments(true, false);
		if(exportedDocs.size() > 0) {
			int dc = exportedDocs.size();
			Log.d(TAG, "Sending docs " + Integer.toString(dc));
			FLog.d(String.format("SendDocsService.onHandleIntent Sending docs %d ", dc));
			
			WriteService writeService = (WriteService) RWServiceFactory.instance.createWriteService(exportedDocs);

			Config config = ConfigManager.getConfig();
			LoginData sndInfo = new LoginData(config.login, config.passw, config.impersonate, getApplicationContext());

			String errMessage = "";
			boolean result = false;
			if (!writeService.write(getApplicationContext(), sndInfo)) {
				errMessage = writeService.getMessage();
				Log.d(TAG, "Doc are exported: FAILURE " + errMessage);
				FLog.d("Doc are exported: FAILURE " + errMessage);
			} else {
				Log.d(TAG, "Doc are exported: SUCCESS");
				FLog.d("Doc are exported: SUCCESS");
				result = true;
			}
			
			if(Features.SYNC_INFO) {
				SyncInfoImpl syncInfoImpl = new SyncInfoImpl();
				SyncInfo syncInfo = syncInfoImpl.getData();
				syncInfo.created = Util.getDateTime();
				syncInfo.syncparam = SyncInfo.DOCS;
				syncInfo.params = 0;
				syncInfo.result = result ? 1 : 0;
				syncInfo.errmsg = errMessage == null ? "" : errMessage;

				syncInfo.login = config.login;
				syncInfo.password = config.passw;
				syncInfo.ip1 = config.address;
				syncInfo.ip2 = config.address2;
				syncInfo.port1 = config.port;
				syncInfo.port2 = config.port2;
				syncInfo.restore = 0;
				
				syncInfoImpl.write();
				syncInfoImpl.close();
			}
			
		}
		
		FLog.d("SendDocsService.onHandleIntent finish");
	}
}
