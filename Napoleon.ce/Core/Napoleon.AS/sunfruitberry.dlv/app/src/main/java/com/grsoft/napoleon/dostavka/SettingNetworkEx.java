package com.grsoft.napoleon.dostavka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

public class SettingNetworkEx extends SettingNetwork {
	@Override protected int getPreferenceResource() { return R.xml.network_pref_ex;}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Preference button = findPreference(getString(R.string.restoreDocs));
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	        @Override
	        public boolean onPreferenceClick(Preference preference) {
	        	doRestore();
	            return true;
	        }
	    });
	}
	
	@SuppressLint("SimpleDateFormat")
	void doRestore() {
		ListPreference monthPref = (ListPreference) findPreference(getString(R.string.restoreDocsMonth));
		int month = Integer.parseInt(monthPref.getValue());
		
		
		((SettingEx)getActivity()).doRestore(month);
	}
}

/*

Route
RouteItem
Waybill
RoutePoint
Price

DispatchDoc
DShipmentDoc

*/