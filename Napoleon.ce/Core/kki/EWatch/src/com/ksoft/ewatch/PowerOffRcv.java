package com.ksoft.ewatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class PowerOffRcv extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("PowerOffRcv", "PowerOffRcv ");
		
		SharedPreferences pref = context.getSharedPreferences(Main.PREFNAME, Context.MODE_PRIVATE);
		
		if(pref.getBoolean(Main.SMSACTIVE, false))
			SMSHelper.send(context, pref.getString(Main.PHONE, ""), pref.getString(Main.POWEROFFTEXT, context.getString(R.string.power_off_text)));
	}

}
