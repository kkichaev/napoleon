/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   21/04/2011   creating
 */
package com.grsoft.napoleon;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.ProgramSettings;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.SettingActivity;
import com.grsoft.util.Util;

/***
 * Панель табов - настройки программы
 * @author kki
 *
 */
public class Setting extends TabActivity{
	
	static final String ADMPWD = "ADMPWD"; 
	private static final int EXPORT_DLG_ID = 0;
	private static final int IMPORT_DLG_ID = 1;
	private static final int PASSWORD_DLG_ID = 2;
	
	//public static final String WAREHOUSE_TAB = "warehouse";
	private static final String ACTIVE_TAB = "activeTab";
	
	protected List<String> tabsActivities = new ArrayList<String>(); 
	
	public static Class<? extends SettingActivity> NetworkSettingActivity = Configuration.class;
	public static Class<? extends SettingActivity> WarehouseSettingActivity = WarehouseSetting.class;
	public static Class<? extends SettingActivity> BehaviorSettingActivity = BehaviorSettingW.class;
	public static Class<? extends SettingActivity> GPSSettingActivity = GpsSetting.class;
	public static Class<? extends SettingActivity> PhotoSettingActivity = PhotoSetting.class;
	public static Class<? extends Activity> activity = Setting.class;
	
	protected String openTag = null;
	
	private String adminPassword = "";
	
	/**
	 * Дополнительные вкладки
	 */
	public static ArrayList<Class<? extends SettingActivity>> addTabs = new ArrayList<Class<? extends SettingActivity>>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
		if( b != null )
			openTag = b.getString(ACTIVE_TAB);
		
		setContentView(R.layout.setting);
//		ConfigImpl ci = new ConfigImpl();
//		ci.getData().key = ADMPWD;
//		if( ci.read() )
//			adminPassword = ci.getData().value;
//		ci.close();
//		if( adminPassword.length() != 0 )
//			showDialog(PASSWORD_DLG_ID);
//		else
			updatesTabs(true);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if( openTag != null )
			outState.putString(ACTIVE_TAB, openTag);
	}
	
	protected boolean canCreateForUser(Class<? extends SettingActivity> activity) {
		return activity != NetworkSettingActivity && activity != GPSSettingActivity;
	}

	protected void updatesTabs(boolean isAdmin) {
		tabsActivities.clear();
		
		if( isAdmin || canCreateForUser(NetworkSettingActivity) )
			createTabSpec(NetworkSettingActivity);
		
		if (RuntimeEnv.isPhotoSupported() && (isAdmin || canCreateForUser(PhotoSettingActivity)))
			createTabSpec(PhotoSettingActivity);
		
		if( isAdmin || canCreateForUser(BehaviorSettingActivity) )
			createTabSpec(BehaviorSettingActivity);
		
		if( isAdmin || canCreateForUser(GPSSettingActivity) )
			createTabSpec(GPSSettingActivity);
		
		if( isAdmin || canCreateForUser(WarehouseSettingActivity) )
			createTabSpec(WarehouseSettingActivity);
		
		for(Class<? extends SettingActivity> ca : addTabs)
			if( isAdmin || canCreateForUser(ca) )
				createTabSpec(ca);
		
		if( openTag != null )
			getTabHost().setCurrentTabByTag(openTag);
	}

//	protected void createGPSSettingTab() {
//		createTabSpec(GpsSetting.class);
//	}

//	protected void createBehaviorSettingTab() {
//		createTabSpec(BehaviorSettingActivity);
//	}

//	public void createPhotoSettingTab(){
//		createTabSpec(PhotoSetting.class);
//	}
	
//	public void createNetworkSettingTab() {
//		createTabSpec(Configuration.class);
//	}
//
//	protected void createWarehouseSettingTab() {
//		createTabSpec(WarehouseSettingActivity);
//	}
	
	protected void createTabSpec(Class<? extends SettingActivity> tabPage){
		TabHost th = getTabHost();
		
		try {
			SettingActivity sa = tabPage.newInstance();
			String caption = getString(sa.getName());		
			
			String tag = tabPage.getCanonicalName();
			
			TabHost.TabSpec ts = th.newTabSpec(tag);
			ts.setIndicator(caption, getResources().getDrawable(sa.getIcon()));
			ts.setContent(new Intent().setClass(this, tabPage));
			
			th.addTab(ts);
			
			tabsActivities.add(tag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);		
	}
	
	public static void open(Context context, Class<? extends SettingActivity> tab) {
		Intent i = new Intent(context, activity);
		i.putExtra(ACTIVE_TAB, tab.getCanonicalName());
		context.startActivity(i);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting_opt_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.itSave) {
			saveAllChanges();
			finish();
		} else if (item.getItemId() == R.id.itExport) {
			showDialog(EXPORT_DLG_ID);
		} else if (item.getItemId() == R.id.itImport) {
			showDialog(IMPORT_DLG_ID);
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void saveAllChanges() {
		for (int i = 0; i< tabsActivities.size(); i++){
			String tag = tabsActivities.get(i);
			SettingActivity act = (SettingActivity)getLocalActivityManager().getActivity(tag);
			
			if (act != null)
				act.save();
		}
		
		if(Features.SEND_PROGRAM_SETTINGS) {
			com.grsoft.napoleon.util.CfgNplW config = (com.grsoft.napoleon.util.CfgNplW) ConfigManager.getConfig();
			ProgramSettings.saveSettings(config);
		}
	}
	
	@Override
	protected void onResume() {
		com.grsoft.napoleon.util.CfgNplW config = (com.grsoft.napoleon.util.CfgNplW) ConfigManager.getConfig();
		config.setOrientation(this);
		super.onResume();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case EXPORT_DLG_ID:
		case IMPORT_DLG_ID:
			return makeExportImportDlg(id);
		case PASSWORD_DLG_ID:
			return askPasswordDlg();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	private Dialog askPasswordDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.input_passw, null));
		builder.setTitle(R.string.settings);
		builder.setMessage(R.string.input_password);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText edPassword = (EditText) ((AlertDialog)dialog).findViewById(R.id.edPassword);
				boolean isAdmin = edPassword.getText().toString().equals(adminPassword);
				updatesTabs(isAdmin);
			}
		});
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override public void onCancel(DialogInterface dialog) { updatesTabs(false); }
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { updatesTabs(false); }
		});
		
		return builder.create();
	}

	private Dialog makeExportImportDlg(final int type){
		final String EXPORT_TITLE = getString(R.string.save_setting);
		final String IMPORT_TITLE = getString(R.string.load_setting);
		final View view = View.inflate(this, R.layout.inputfilename, null);
		final int BASE_EXPORT_ID = 333;
		
		abstract class OKClickListener implements OnClickListener{
			int dlgId;
			View parentView;
			
			public OKClickListener(int dlgId, View view){ this.dlgId = dlgId; parentView = view; }
			
			@Override public void onClick(View view) {
				dismissDialog(dlgId);
				
				EditText edInput = (EditText) parentView.findViewById(R.id.edInput);
				CheckBox cbBaseExport = (CheckBox) parentView.findViewById(BASE_EXPORT_ID);
				getTask().execute(edInput.getText().toString(), cbBaseExport.isChecked());
			}
			
			abstract AsyncTask<Object, Void, Boolean> getTask();
			
		}
		
		class ExportListener extends OKClickListener{
			
			public ExportListener(View view) { super(EXPORT_DLG_ID, view); }
						
			public void onClick(View v) {
				saveAllChanges();
				super.onClick(v);
			}

			@Override AsyncTask<Object, Void, Boolean> getTask() { return new ExportTask(); };
		}
		
		class ImportListener extends OKClickListener{
			
			public ImportListener(View view) { super(IMPORT_DLG_ID, view); }
			
			@Override AsyncTask<Object, Void, Boolean> getTask() { return new ImportTask(); };
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setView(view);
		builder.setTitle(type == EXPORT_DLG_ID ? EXPORT_TITLE : IMPORT_TITLE);
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		LinearLayout llInputField = (LinearLayout) view.findViewById(R.id.llInputField);
		CheckBox cbBaseExport = new CheckBox(this);
		cbBaseExport.setId(BASE_EXPORT_ID);
		cbBaseExport.setText(type == EXPORT_DLG_ID ? R.string.export_base : R.string.import_base);
		llInputField.addView(cbBaseExport);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog(type);
			}
		});
		
		Button btnOK = (Button) view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(type == EXPORT_DLG_ID ? 
				new ExportListener(view) : new ImportListener(view)); 
		
		return builder.create();
	}
	
	class ExportTask extends AsyncTask<Object, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Object... params) {
			try{
				String distFlolderName = (String) params[0];
				ConfigManager.exportXml(distFlolderName);
				
				boolean baseShouldBeCopied = (Boolean) params[1];
				
				if (baseShouldBeCopied){
					File src = new File(Path.getDataBasePath());
					File sdcard = Environment.getExternalStorageDirectory();
					File dist = new File(new File(sdcard, distFlolderName), Path.BASE_NAME);
					Util.copy(src,dist);
				}
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			Toast.makeText(Setting.this, 
					result? R.string.file_save_succs : R.string.file_save_error, Toast.LENGTH_LONG).show();
		}
	}
	
	class ImportTask extends AsyncTask<Object, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Object... params) {
			try{
				ConfigManager.importXml((String) params[0]);
				
				boolean baseShouldBeCopied = (Boolean) params[1];
				
				if (baseShouldBeCopied){
					String srcFlolderName = (String) params[0];
					File dest = new File(Path.getDataBasePath());
					File sdcard = Environment.getExternalStorageDirectory();
					File src = new File(new File(sdcard, srcFlolderName), Path.BASE_NAME);
					SQLiteDatabase db = DataBaseManager.getDataBase();
					if(db.isOpen())
						db.close();
					Util.copy(src,dest);
					
					Context context = Setting.this;
					Intent mStartActivity = new Intent(context, RuntimeEnv.getMainActivity(context));
					int mPendingIntentId = 123456;
					PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,
							mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
					AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
					mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
					System.exit(0);					
				}
				return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			
			if (result){
				ConfigManager.save();
				Toast.makeText(Setting.this, 
					R.string.setting_has_been_read_succs, Toast.LENGTH_LONG).show();
				Activity act = Setting.this.getCurrentActivity();
				((SettingActivity)act).update();
			} else
				Toast.makeText(Setting.this, 
					R.string.setting_load_error, Toast.LENGTH_LONG).show();
		}
	}
}
