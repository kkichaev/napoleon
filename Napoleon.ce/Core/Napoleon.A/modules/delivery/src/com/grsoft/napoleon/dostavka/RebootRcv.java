package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.RoutePointImpl;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class RebootRcv extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (RoutePointImpl.isRouteComplete()){
			Intent i = new Intent(context, MainService.class);
			context.stopService(i);
		}
	}

}
