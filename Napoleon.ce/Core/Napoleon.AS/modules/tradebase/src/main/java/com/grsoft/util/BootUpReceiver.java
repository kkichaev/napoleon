package com.grsoft.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.grsoft.napoleon.Napoleon;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d(getClass().getCanonicalName(), "BootUpReceiver.onReceive()");
		
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		
		if (!config.isAutostart)
			return;
		
		if (config.isService){
			Intent intent = new Intent(context, Napoleon.serviceType);  
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startService(intent);
		}else{
			Intent intent = new Intent(context, RuntimeEnv.getMainActivity(context));  
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(intent);
		}
	}
}
