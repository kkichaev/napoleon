package com.grsoft.ads;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.MainExceptionHandler;
import com.grsoft.util.Util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class AdsNew extends SyncActivity implements OnDateSetListener, OnItemClickListener {
	private static final int TASK_SELCT_DLG = 1;
	private static String TASK_MSG = "task_msg";
	public static final String ADMPWD = "ADMPWD";
	public static final String CONTEXT_MENU_SHOW_ACTION = "context_menu_show_action";
	private static final int OPEN_CONTEXT_MENU_DLG = R.id.open_context_menu_dlg;
	private static final int PERMISSION_REQUEST = 0;
	public static String RELOAD_ACTION = "com.grsoft.ads.Ads.RELOAD_ACTION";
	
	private BroadcastReceiver contextMenu = new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) {	showDialog(OPEN_CONTEXT_MENU_DLG);}};
	private SimpleDateFormat dayFmt = new SimpleDateFormat("dd");
	private Date date = Util.getDate();
	private ListView list;
	private AdsNewAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));
		setContentView(R.layout.main);
		
		adapter = new AdsNewAdapter(this);
		
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
		list.setAdapter(adapter);
		
		if(Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
				ActivityCompat.requestPermissions(this,
		                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
		                		Manifest.permission.WRITE_EXTERNAL_STORAGE,
		                		Manifest.permission.CALL_PHONE},
		                PERMISSION_REQUEST);
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
		if(rc == PERMISSION_REQUEST) {
			for(int i = 0; i < result.length; i++)
				if (result[i] != PackageManager.PERMISSION_GRANTED) {
					exit();
					break;
				}
			
			if (service != null)
				service.initgps();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		reload();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(contextMenu, new IntentFilter(CONTEXT_MENU_SHOW_ACTION));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(contextMenu);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TASK_SELCT_DLG:
			return createTaskSelectDlg();
		case OPEN_CONTEXT_MENU_DLG:
			return createContextMenuDlg();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = false;
		
		final MenuItem i = menu.findItem(R.id.itCal);
    	
    	if(i != null){
    		View v = i.getActionView();
    		if(v != null){
    			TextView tv = (TextView) v.findViewById(R.id.tvCurDate);
    			tv.setText(dayFmt.format(date));
    			result = true;
    		}
    	}
    	
		return result;
	}

	private Dialog createContextMenuDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Meню");
		builder.setItems(new String[]{"Показать на карте", "Выбрать карту..."}, null);
		return builder.create();
	}

	private Dialog createTaskSelectDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Задача");
		builder.setMessage("message");
		builder.setNegativeButton("Отмена", null);
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		case TASK_SELCT_DLG:
			prepareTaskSelectDlg(dialog, args);
			break;
		default:
			super.onPrepareDialog(id, dialog, args);
		}
	}

	private void prepareTaskSelectDlg(Dialog dialog, Bundle args) {
		((AlertDialog) dialog).setMessage(args.getString(TASK_MSG));
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main_opt_menu, menu);
		
		final MenuItem i = menu.findItem(R.id.itCal);
    	
    	if(i != null)
    		i.getActionView().setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { 
					menu.performIdentifierAction(i.getItemId(), 0);	} });
    	
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.itConfig) {
			Intent intent = new Intent("com.grsoft.ads.SettingNew.OPEN");
			startActivity(intent);
		} else if (itemId == R.id.itUpdate) {
			sync();
		} else if (itemId == R.id.itClose)
			exit();
		else if (itemId == R.id.itAbout)
			About.show(this);
		else if (itemId == R.id.itNewTask)
			NewTaskList.open(this);
		else if (itemId == R.id.itMsgList)
			MessageList.open(this);
		else if (item.getItemId() == R.id.itCal)
        	openCal();
		else if (itemId == R.id.itClear)
			ClearActivity.open(this);

		return true;
	}

	private void openCal() {
    	DatePickerDialog dlg = new DatePickerDialog(this, this, date.getYear() + 1900, date.getMonth(), date.getDate());
    	dlg.show();
	}
	
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
		date = new Date(year - 1900, monthOfYear, dayOfMonth);
		adapter.reload(date);
		invalidateOptionsMenu();
	}
	
	protected void exit() {
		finish();
		((AdsApp)getApplication()).exit();
	}
	
	@Override
	protected void onSyncFinished(boolean result) {	if(result){ reload();}}

	protected void reload() {
		adapter.reload(date);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TaskQuery t = (TaskQuery) parent.getItemAtPosition(position);
		
		if(t != null){
			if(t.solution == TaskQuery.NEW)
				TaskPreview.open(this, t.taskid);
			else if(t.solution == TaskQuery.APPLY)
				TaskReadyToStart.open(this, t.taskid);
			else if (t.solution == TaskQuery.INWORK) 	
				TaskEdit.open(this, t.taskid);
		}
	}
}
