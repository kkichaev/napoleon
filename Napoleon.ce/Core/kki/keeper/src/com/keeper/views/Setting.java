package com.keeper.views;

import com.keeper.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class Setting extends PreferenceActivity {
	private static final String TAG = "Setting";
	public static final String SHARED_PREFERENCES_NAME = "KeeperSetting";
	public static final String LOGIN = "login";
	public static final String PASSWORD_LEN = "passw_len";
	public static final String DEF_PASSWORD_LEN_VALUE = "6";
	public static final String SHOW_PASSW="show_passw";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SHARED_PREFERENCES_NAME);
		addPreferencesFromResource(R.xml.setting);
		
		EditTextPreference login = (EditTextPreference) findPreference(LOGIN);
		
		login.setSummary(getSummary(login, R.string.login_summary));
		login.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Log.d(TAG, "onPreferenceChange");
				
				((EditTextPreference)preference).setText(((String)newValue).trim());
				preference.setSummary(getSummary(
						(EditTextPreference)preference, R.string.login_summary));
				
				return false;
			}
		});
		
		EditTextPreference passw_len = (EditTextPreference) findPreference(PASSWORD_LEN);
		passw_len.setSummary(getResources().getString(R.string.passw_len_summary, passw_len.getText()));
		passw_len.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				try{
					((EditTextPreference)preference).setSummary(
							getResources().getString(R.string.passw_len_summary, 
									Integer.parseInt((String)newValue)));
					return true;
				}catch(Exception e){
					return false;
				}
			}
		});
	}

	public String getSummary(EditTextPreference pref, int resSummaryId){
		return pref.getText() == null || pref.getText().length() == 0 ?
				getResources().getString(resSummaryId) :
				pref.getText();
	}
	
	public static void open(Context context){
		Intent intent = new Intent(context, Setting.class);
		context.startActivity(intent);
	}
}
