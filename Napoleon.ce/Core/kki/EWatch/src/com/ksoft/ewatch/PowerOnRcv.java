package com.ksoft.ewatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class PowerOnRcv extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("PowerOnRcv", "PowerOnRcv ");
		
		SharedPreferences pref = context.getSharedPreferences(Main.PREFNAME, Context.MODE_PRIVATE);
		
		if(pref.getBoolean(Main.SMSACTIVE, false))
			SMSHelper.send(context, pref.getString(Main.PHONE, ""), pref.getString(Main.POWERONTEXT, context.getString(R.string.power_on_text)));

	}

}
