package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Setting extends PreferenceActivity{
private static List<String> fragments = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.prefs, target);
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
