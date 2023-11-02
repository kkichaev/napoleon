package com.grsoft.napoleon;

import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	
	static final String SYNC_ALL = "SyncAll";
	
	public static void openSyncAll(Context context) {
		Intent i = new Intent(context, UpdateDBEx.class);
		i.putExtra(SYNC_ALL, true);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getIntent().getBooleanExtra(SYNC_ALL, false)) {
			((CheckBox) findViewById(R.id.cbClearDB)).setChecked(true);
			((CheckBox) findViewById(R.id.cbRemains)).setChecked(false);
			((CheckBox) findViewById(R.id.cbDocs)).setChecked(true);
			((CheckBox) findViewById(R.id.cbVisit)).setChecked(true);
			((CheckBox) findViewById(R.id.cbGenData)).setChecked(true);
			((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
			((CheckBox) findViewById(R.id.cbPresent)).setChecked(true);
			((CheckBox) findViewById(R.id.cbRecreateStory)).setChecked(true);
			
			findViewById(R.id.btnUpdate).performClick();
		}
	}
	
	@Override
	protected UpdateProcess getUpdateProcess() {
		if( VersionChecker.isLastVersion(this, true) == false ) {
			showDialog(R.id.need_update_version);			
			return null;
		}
		return super.getUpdateProcess();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.need_update_version)
			return VersionChecker.createAlertDialog(this); 
		return super.onCreateDialog(id);
	}

	@Override
	protected void postSync(Boolean result) {
		if(result) {
			SharedPreferences sp = getSharedPreferences(DocumentsEx.LAST_SYNC, MODE_PRIVATE);
			Date d = new Date();
			SharedPreferences.Editor e = sp.edit();
			e.putLong(DocumentsEx.LAST_SYNC, d.getTime());
			e.commit();
		}
	}
}
