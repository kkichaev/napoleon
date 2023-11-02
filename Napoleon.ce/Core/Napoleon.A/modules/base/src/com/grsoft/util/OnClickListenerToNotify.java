/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   02/05/2011   creating
 */
package com.grsoft.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

/***
 * Обработчик OnClickListener 
 * при вызове посылается уведомления
 * для вибрации, если включена конфиг опция.
 *  
 * @author kki
 *
 */
public class OnClickListenerToNotify implements OnClickListener {

	@Override
	public void onClick(View v) {
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		
		if (config.vibration){
			NotificationManager nm = (NotificationManager) v.getContext().
				getSystemService(Activity.NOTIFICATION_SERVICE);
			Notification notif = new Notification();
			notif.vibrate = new long[]{ 0, 35};
			nm.notify(1001, notif);
		}

	}

}
