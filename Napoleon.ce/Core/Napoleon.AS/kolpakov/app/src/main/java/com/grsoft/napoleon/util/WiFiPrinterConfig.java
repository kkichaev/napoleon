package com.grsoft.napoleon.util;

import android.content.Context;
import android.content.SharedPreferences;

public class WiFiPrinterConfig {
	private static final String PREF_NAME = "WiFiPrinterConfig";
	private static final String COPIES = "copies";
	private static final String IP = "ip";
	private static final String PORT = "port";
	
	public String ip = "";
	public int port = 0;
	public int copies = 1;
	
	public static WiFiPrinterConfig get(Context context) {
		WiFiPrinterConfig cfg = new WiFiPrinterConfig();
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		
		cfg.copies = sp.getInt(COPIES, 1);
		cfg.ip = sp.getString(IP, "192.168.43.50");
		cfg.port = sp.getInt(PORT, 9100);
		
		return cfg;
	}
	
	public void put(Context context) {
		SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(COPIES, copies);
		editor.putInt(PORT, port);
		editor.putString(IP, ip);
		editor.commit();		
	}
}
