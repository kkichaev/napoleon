package com.grsoft.ads;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.MainExceptionHandler;

public class Ads extends SyncActivity {
	private static final int TASK_SELCT_DLG = 1;
	private static String TASK_MSG = "task_msg";
	private ViewPager viewPager;
	public static final String ADMPWD = "ADMPWD";
	public static final String CONTEXT_MENU_SHOW_ACTION = "context_menu_show_action";
	private static final int OPEN_CONTEXT_MENU_DLG = R.id.open_context_menu_dlg;
	public static String RELOAD_ACTION = "com.grsoft.ads.Ads.RELOAD_ACTION";
	
	private TaskPageAdapter adapter;
	private BroadcastReceiver contextMenu = new BroadcastReceiver() { @Override public void onReceive(Context context, Intent intent) {	showDialog(OPEN_CONTEXT_MENU_DLG);}};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));
		
		viewPager = new ViewPager(this);
		viewPager.setId(R.id.pager);
		
		setContentView(viewPager);
		
		adapter = new TaskPageAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(adapter.getCount() / 2);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_opt_menu, menu);
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

		return true;
	}

	protected void exit() {
		finish();
		((AdsApp)getApplication()).exit();
	}
	
	@Override
	protected void onSyncFinished(boolean result) {	if(result){ reload();}}

	protected void reload() {
		AdsFragment f = (AdsFragment) adapter.getItem(viewPager.getCurrentItem());
		f.refresh();
	}
}
