package com.grsoft.ads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.grsoft.util.DataBaseAdapter;

public class AdsEx extends Ads {
	private BroadcastReceiver updateList; 
	public static final String UPDATE_LIST_ACTION = "com.grsoft.ads.UPDATE_LIST_ACTION";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnUserOrder).setVisibility(View.GONE);
		
		updateList = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				((DataBaseAdapter<?>)lvOrders.getAdapter()).notifyDataSetChanged();
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(updateList, new IntentFilter(UPDATE_LIST_ACTION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(updateList);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.itUserOrder).setVisible(false);
		return true;
	}
}
