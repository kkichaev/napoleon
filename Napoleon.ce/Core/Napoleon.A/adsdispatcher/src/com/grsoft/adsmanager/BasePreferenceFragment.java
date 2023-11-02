package com.grsoft.adsmanager;

import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;


public abstract class BasePreferenceFragment extends PreferenceFragment 
	implements OnSharedPreferenceChangeListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(getPreferenceResource());
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		List<Preference> list = getPreferences();
		if(pref != null && list != null)
			for(Preference p : list)
				if(p != null)
					setSummary(pref, p);
	}
	
	private void setSummary(SharedPreferences pref, Preference p ){
		String summary = pref.getString(p.getKey(), "");
		
		if(p instanceof RingtonePreference)
			summary = inflateRingtoneName(summary);
		
		p.setSummary(summary);
	}

	private String inflateRingtoneName(String val) {
		String result = "";
		
		try{
			Uri u = Uri.parse(val);
			Ringtone r = RingtoneManager.getRingtone(getActivity(), u);
			result = r.getTitle(getActivity());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	private List<Preference> getPreferences(){
		List<Preference> result = new ArrayList<Preference>();
		
		PreferenceScreen screen = getPreferenceScreen();
		if(screen != null)
			for(int i = 0; i < screen.getPreferenceCount(); i++){
				Preference p = screen.getPreference(i);
				inflateChild(p, result);
			}
		
		return result;
	}
	
	private void inflateChild(Preference p, List<Preference> out){
		if(p instanceof PreferenceCategory){
			PreferenceCategory pc = (PreferenceCategory)p;
			
			for(int i = 0; i < pc.getPreferenceCount(); i ++){
				Preference pp = pc.getPreference(i); 
				inflateChild(pp, out);
			}
				
		}else
			out.add(p);
	}
	
	protected abstract int getPreferenceResource() ;

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference pref = findPreference(key);
		
		if(pref != null)
			setSummary(sharedPreferences, pref);
	}

	@Override
	public void onStart() {
		super.onStart();
		setOnSharedPreferenceChangeListener(true);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		setOnSharedPreferenceChangeListener(false);
	}
	
	private void setOnSharedPreferenceChangeListener(boolean enable){
		SharedPreferences p = getSharedPreferences();
		
		if(p != null){
			if (enable)
				p.registerOnSharedPreferenceChangeListener(this);
			else
				p.unregisterOnSharedPreferenceChangeListener(this);
		}
	}
	
	private SharedPreferences getSharedPreferences(){
		SharedPreferences result = null;
		
		PreferenceManager pm = getPreferenceManager();
		
		if(pm != null)
			result = pm.getSharedPreferences();
		
		return result;
	}
}
