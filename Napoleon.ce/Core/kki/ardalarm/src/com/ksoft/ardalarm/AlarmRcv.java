package com.ksoft.ardalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmRcv extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent intent = new Intent(context, SoundPlay.class);
		context.startService(intent); 
	}

}
