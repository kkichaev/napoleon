package com.grsoft.napoleon;

import java.util.Date;
import com.grsoft.network.NetworkAsyncTask;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	public static String PREF_NAME = "UpdateDBEx .PREFERENCES";
	public static String LAST_SYNC = "last_sync";
	private static final String OPEN_BLOCKED = "open_blocked";
	boolean blocked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		blocked = (b==null) ? false : b.getBoolean(OPEN_BLOCKED, false);
		super.onCreate(savedInstanceState);
	}
	
	public static void openBlocked(Context c) {
		Intent i = new Intent(c, UpdateDBEx.class);
		i.putExtra(OPEN_BLOCKED, true);
		c.startActivity(i);
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
	}
	
	@Override
	public void onBackPressed() {
		if (!blocked)
			super.onBackPressed();
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		Editor ed = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
		ed.putLong(LAST_SYNC, new Date().getTime());
		ed.commit();
		
		return super.onFinishUpdate(task);
	}
}
