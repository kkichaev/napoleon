package com.grsoft.adsmanager;

import com.grsoft.network.UpdateProcess.Params;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ParamsHelper {
	public Params createParams(Context context) {
		Params p = new Params();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String login = pref.getString(Setting.LOGIN, "");
		String pass = pref.getString(Setting.PASSWORD, "");
		String ip1 = pref.getString(Setting.IP1, "");
		String ip2 = pref.getString(Setting.IP2, "");
		
		final int DEFAULT_PORT = 8888;
		int port1 = DEFAULT_PORT;
		int port2 = DEFAULT_PORT;
		
		try{
			port1 = Integer.parseInt(pref.getString(Setting.PORT, ""));
			port2 = port1;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		p.ip1 = ip1;
		p.ip2 = ip2;
		p.port1 = port1;
		p.port2 = port2;
		p.login = login;
		p.pass = pass;
		
		//p.impersonate 
		
		return p;
	}
}
