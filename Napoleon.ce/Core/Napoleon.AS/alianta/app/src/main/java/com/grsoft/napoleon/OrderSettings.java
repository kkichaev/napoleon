package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;

public class OrderSettings {
	static final String PREF_NAME = "OrderSettings";
	static final String CASH_TAG = "Cash";
	static final String DLV_METHOD = "DlvMethod";
	static final String CTRL_TYPE = "CtrlType";
		
	public boolean cash;
	public String dlvMethod = "";
	public String ctrlType = "";
	
	public void save(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean(CASH_TAG, cash);
		edit.putString(DLV_METHOD, dlvMethod);
		edit.putString(CTRL_TYPE, ctrlType);
		edit.commit();
	}
	
	public static OrderSettings load(Context ctx) {
		OrderSettings os = new OrderSettings();
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		os.cash = sp.getBoolean(CASH_TAG, false);
		os.ctrlType = sp.getString(CTRL_TYPE, "");
		os.dlvMethod = sp.getString(DLV_METHOD, "");
		
		return os;
	}
}
