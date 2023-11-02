package com.grsoft.ads;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingNew extends PreferenceActivity {
	public static final String LOGIN = "login";
	public static final String PASSWORD = "password";
	public static final String IP1 = "ip1";
	public static final String IP2 = "ip2";
	public static final String PORT = "port";
	
	private static List<String> fragments = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
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
