package com.ksoft.ardalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	BroadcastReceiver status = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, intent.getStringExtra("status"),
					Toast.LENGTH_LONG).show();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		registerReceiver(status, new IntentFilter("status"));

		FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		tabHost.addTab(
				tabHost.newTabSpec(Alarm.class.toString()).setIndicator(
						getString(R.string.alarm)), Alarm.class, null);
		
		// TabHost.TabSpec ts = th.newTabSpec(Alarm.class.getCanonicalName());
		// ts.setIndicator(getString(R.string.alarm), null);
		// ts.setContent(new Intent().setClass(this, Alarm.class));
		//
		// th.addTab(ts);
		//
		// ts = th.newTabSpec(TimerView.class.getCanonicalName());
		// ts.setIndicator(getString(R.string.timer), null);
		// ts.setContent(new Intent().setClass(this, TimerView.class));
		//
		// th.addTab(ts);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(Setting.ACTION));
			return true;
		case R.id.add_alarm:
			startActivity(new Intent(CreateAlarm.ACTION));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		// unregisterReceiver(status);
		super.onPause();
	}
}
