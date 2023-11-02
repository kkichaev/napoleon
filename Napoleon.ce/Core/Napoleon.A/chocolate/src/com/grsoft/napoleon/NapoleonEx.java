package com.grsoft.napoleon;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.util.Consts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class NapoleonEx extends Napoleon {
	protected String getOrgReadingFields() { return "name,id,address,color,flags,debt"; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		int count = 0;
		String tableName = DataObjectInfo.getInstance().getTableName(AgentRcv.class);
		Cursor c = null;
		try {
			c = DataBaseManager.getDataBase().rawQuery("select count(*) from " + tableName + " where id=userid", null); 
			if( c.moveToNext() )
				count = c.getInt(0);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if( c != null )
				c.close();
		}
		
		super.onCreate(savedInstanceState);
		
		if(count == 0) {
			UpdateDBEx.openBlocked(this);
		} else {
			screenOn = new BroadcastReceiver() {
				
				@Override
				public void onReceive(Context context, Intent intent) {
		            Log.d(Consts.D_TAG, "MyReceiver");

		            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
		                Log.d(Consts.D_TAG, "Screen ON");
		            }
		            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
		                Log.d(Consts.D_TAG, "Screen OFF");
		                LoginActivity.open(context);
		            }
		         }
			};
			
			registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
			registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_OFF));
			LoginActivity.open(this);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LoginActivity.open(this);
	}
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(screenOn != null) {
			unregisterReceiver(screenOn);
			screenOn = null;
		}
	}
	
	BroadcastReceiver screenOn;
}
