package com.grsoft.database;

import com.grsoft.dataobjects.DispatchReturnsInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.DocListEx;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.ExtrasConst;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

public class DispatchReturnsHitching extends Hitching {
	
	static int NOTIFY_ID = 0x123;
	boolean readNew = false;
	Context context;
	
	
	public DispatchReturnsHitching(Context context) {
		super(DispatchReturnsInfo.class);
		dbProxy.setUpsert(false);
		this.context = context;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		readNew = false;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DispatchReturnsInfo dobj = (DispatchReturnsInfo) rawObject.createDataObject(dataObject);
		dobj.date = dobj.created;
		dobj.params |= (ParamState.ofExported | ParamState.ofProceeded);
		if(dbProxy.insertRecord(dobj) != ExtrasConst.INVALID_ID)
			readNew = true;
		postRead(dobj);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if(readNew) {
			final CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
			if(cfg.newReturnAlarm > 0) {
				Intent i = new Intent(context, DocListEx.class);
				i.putExtra(DocListEx.SHOW_RETURNS, true);
		        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i, 0);
		        Notification noti = new NotificationCompat.Builder(context)
		                .setContentTitle("Новый возврат")
		                .setContentText("Поступил новый возврат")
		                .setSmallIcon(R.drawable.return_notify)
		                .setAutoCancel(true)
		                .setContentIntent(contentIntent)
		                .build();
		       		
		        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		       	nm.notify(NOTIFY_ID, noti);
				
				if(cfg.newReturnSound.length() > 0) {
					Thread t = new Thread(new Runnable() {
						
						@Override
						public void run() {
							MediaPlayer mp = new MediaPlayer();
							Uri notification = Uri.parse(cfg.newReturnSound);
							try {
								mp.setDataSource(context, notification);
								mp.prepare();
								mp.start();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					t.start();
		       	    Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		       	    v.vibrate(500);
				}
			}
			readNew = false;
		}
	}

}
