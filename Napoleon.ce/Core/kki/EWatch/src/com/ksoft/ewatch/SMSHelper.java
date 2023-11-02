package com.ksoft.ewatch;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSHelper {

	public static void send(Context context, String phone, String text) {
		if (phone != null && phone.trim().length() > 0 && text != null && text.trim().length() > 0)
			try{
				SmsManager manager = SmsManager.getDefault();
				manager.sendTextMessage(phone, null, text, null, null);
				
				Log.d("SMSHelper", "Message SENDED! " + text);
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}

}
