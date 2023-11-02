package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Setting extends PreferenceActivity{
private static List<String> fragments = new ArrayList<String>();
	public static Class<? extends Setting> activity = Setting.class;

	public static int XML_PREFS = R.xml.prefs;
	
	public static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(XML_PREFS, target);
		updateFragments(target);
	}

	protected void updateFragments(List<Header> target) {
		fragments.clear();
        for (Header header : target) {
            fragments.add(header.fragment);
        }
	}
	
	protected boolean isValidFragment(String fragmentName) { return fragments.contains(fragmentName); }
}
