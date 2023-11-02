package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;

import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.util.RuntimeEnv;

public class StartFromManager extends BroadcastReceiver {
	public static final String PREFERENCE = "preference";
	public static final String OPENSYNC = "opensync";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		android.util.Log.d("StartFromManager", "onReceive");

		try {
			String rcv = intent.getStringExtra(Intent.EXTRA_TEXT);
			String[] data = rcv.split(";");

			if (data.length > 5) {
				CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
				cfg.login = data[0];
				cfg.passw = data[1];
				cfg.address = data[2];
				cfg.address2 = data[3];
				cfg.port = Integer.parseInt(data[4]);
				if(data.length > 6) {
					ServerAnswer sa = new ServerAnswer();
					sa.response = 1;
					sa.message = String.format("%X", Integer.parseInt(data[6]));
					LoginData.putDuration(sa, context);
				}
				cfg.loggable = false;
				
				boolean opensync = !cfg.impersonate.equals(data[5]); 
				cfg.impersonate = data[5];
				ConfigManager.save(context);

				Intent start = new Intent(context,RuntimeEnv.getMainActivity(context));
				start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
				Editor ed = pref.edit();
				ed.putBoolean(OPENSYNC, opensync);
				ed.commit();

				android.util.Log.d("StartFromManager", start.toString());
				
				PendingIntent pending = PendingIntent.getActivity(
						context, 0, start, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
				AlarmManager mgr = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
						pending);

				android.os.Process.killProcess(android.os.Process.myPid());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
