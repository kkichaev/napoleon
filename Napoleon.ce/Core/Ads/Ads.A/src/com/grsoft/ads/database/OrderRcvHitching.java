package com.grsoft.ads.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.grsoft.ads.Setting;
import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.database.DbWriter;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrderRcvHitching extends HitchOnSelect {
	private boolean recieved = false;
	private Context context;
	
	public OrderRcvHitching(Context context) {
		super(DbObject.getDataType(Order.class), "PDAOrder");
		DbWriter.checkDBTable(DbObject.getDataType(Order.class));
		this.context = context;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		Date begin = calendar.getTime();
		
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" userid = '$CURRENT_USERID' and %s >= ToDate('%s 00:00:00') and params = 0",
				"created", simpleDateFormat.format(begin)));
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Order order = (Order)rawObject
			.createDataObject(DbObject.getDataType(Order.class));
		
		OrderImpl orderImpl = new OrderImpl();
		
		orderImpl.getData().created = order.created;
		orderImpl.read();
		orderImpl.close();
		
		if (orderImpl.getData().params == 0 || orderImpl.isRejected()){
			order.params = 0;
			dbProxy.insertRecord(order);
		}
		
		if (!recieved)
			recieved = true;
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		if (recieved){
			Notification notify = new Notification();
			
			SharedPreferences pref = context.getSharedPreferences(
					Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			
			String message_snd = pref.getString(Setting.ORDER_SND, "");
			
			notify.sound = Uri.parse(message_snd);

			if (pref.getBoolean(Setting.VIBRATE, false))
				notify.defaults |= Notification.DEFAULT_VIBRATE;
			
			((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, notify);
		}
			
	}
}
