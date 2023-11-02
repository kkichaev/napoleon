package com.grsoft.napoleon.util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class ProgID {
	public static String getPrgID(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String id = telephonyManager.getDeviceId();
		if( id == null )
			id = "123456";

		return id;
	}
}
