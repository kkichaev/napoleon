package com.grsoft.napoleon;

import java.security.MessageDigest;

import com.grsoft.dataobjects.impl.ConfigImpl;

import android.content.Context;
import android.content.SharedPreferences;

public class PinChecker {
	static int DEFAULT_TRY = 5;
	
	static String PIN_KEY = "pinEnters";
	static String RUN_KEY = "runEntry";
	static String PIN_NAME = "PinChecker";
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}	

	public static String getHash(String pin) {
		String ret = "";
		try {
			byte[] bytesOfMessage;
			bytesOfMessage = pin.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			ret = bytesToHex(thedigest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static int getMaxTry() {
		int ret = DEFAULT_TRY;
		
		ConfigImpl ci = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if(ci.getValue(sb, "ПопытокПИН"))
			ret = Integer.parseInt(sb.toString());
		
		return ret;
	}

	public static boolean getIsRegistred(Context context) {
		SharedPreferences p = context.getApplicationContext().getSharedPreferences(PIN_NAME, Context.MODE_PRIVATE);
		boolean ret = p.getBoolean(RUN_KEY, false);
		return ret;
	}

	public static void setIsRegistred(Context context) {
		SharedPreferences.Editor p = context.getApplicationContext().getSharedPreferences(PIN_NAME, Context.MODE_PRIVATE).edit();
		p.putBoolean(RUN_KEY, true);
		p.commit();
	}
	
	public static int getTryCount(Context context) {
		SharedPreferences p = context.getApplicationContext().getSharedPreferences(PIN_NAME, Context.MODE_PRIVATE);
		int ret = p.getInt(PIN_KEY, 0);
		return ret;
	}
	
	public static void putTryCount(Context context, int count) {
		SharedPreferences.Editor ed = context.getApplicationContext().getSharedPreferences(PIN_NAME, Context.MODE_PRIVATE).edit();
		ed.putInt(PIN_KEY, count);
		ed.commit();
	}
}
