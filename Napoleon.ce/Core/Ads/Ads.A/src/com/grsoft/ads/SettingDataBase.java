package com.grsoft.ads;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

import com.grsoft.ads.utils.LockOwner;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.util.debug.Path;

public class SettingDataBase extends Setting  implements LockOwner{
	private final Lock lock = new ReentrantLock();
	
	@Override
	protected int getSettingId() {
		return R.xml.setting_data_base;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Preference clearBase = findPreference(CLEAR);
		
		if (clearBase != null)
			clearBase.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					
					if ((Boolean)newValue){
						AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
						builder.setTitle("Внимание!");
						builder.setMessage("Удалить всю информацию из базы данных?");
						builder.setPositiveButton("ОК", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								new AsyncTask<Object, Void, Void>(){
									private Context context;
									private String admpwd = "";
									
									@Override
									protected Void doInBackground(Object... params) {
										context = (Context)params[0];
										DataBaseManager.clearBase();
										Path.clearDataDir();
										return null;
									}
									
									protected void onPreExecute() {
										ConfigImpl configImpl = new ConfigImpl();
										configImpl.getData().key = Ads.ADMPWD;
										configImpl.read();
										configImpl.close();
										
										admpwd = configImpl.getData().value;
									};
								
									protected void onPostExecute(Void result) {
										DbWriter writer = new DbWriter();
										Config config = new Config();
										config.key = "Tracking";
										config.value = "GPSroute";
										writer.insertRecord(config);
										
										if (admpwd.length() > 0){
											config.key = Ads.ADMPWD;
											config.value = admpwd;
											writer.insertRecord(config);
										}
										
										writer.close();
										
										Toast.makeText(context, "База данных очищена", 
												Toast.LENGTH_LONG).show();
										getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).
											edit().putBoolean(CLEAR, true).commit();
										((CheckBoxPreference)findPreference(CLEAR)).setChecked(true);
									};
									
								}.execute(((AlertDialog)dialog).getContext());
							}
						});
						
						builder.setNegativeButton("Отменить", null);
						builder.create().show();
						return false;
					}
					
				return false;
			}
		});
		
		Preference recreateOrder = findPreference(RECREATEORDER);
		
		if (recreateOrder != null){
			((CheckBoxPreference)recreateOrder).setChecked(false);
			recreateOrder.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					UpdateProcess process = UpdateProcess.createProcess(
							SettingDataBase.this, SettingDataBase.this);
					process.setRecreateOrder(true);
					process.execute((Void)null);
					newValue = true;
					((CheckBoxPreference)findPreference(CLEAR)).setChecked(false);
					return true;
				}
			});
		}
	}

	@Override
	public Lock getLock() {
		return lock;
	}
}
