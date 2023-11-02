package com.grsoft.dlc.trial;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dlc.DLC;
import com.grsoft.dlc.Preferences;
import com.grsoft.dlc.trial.R;

public class DLCEx extends DLC {
	private final static int LICENSE_DIALOG = 128;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_opt_menu_ex, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itLicense){
			showDialog(LICENSE_DIALOG);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (((DLCAppTrial)getApplication()).isLicensed())
			menu.findItem(R.id.itLicense).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == LICENSE_DIALOG)
			return createLicenseDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createLicenseDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.license, null);
		final EditText edAnswer = (EditText)view.findViewById(R.id.edAnswer);
		builder.setView(view);
		view.findViewById(R.id.btnDone).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setLicenseAnswer(edAnswer.getText().toString().trim());
				dismissDialog(LICENSE_DIALOG);
			}
		});
		
		return builder.create();
	}
	
	protected void setLicenseAnswer(String code) {
		char[] suffix_array = new char[]{'á','à','á','à',' ','ñ',
				'å','ÿ','ë','à',' ','ã','î','ð','î','õ',' ','è',' ',
				'ñ','ê','à','ç','à','ë','à',' ','á','à','á','à',' ','î','õ'};
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String deviceid = tm.getDeviceId() + new String(suffix_array);
		
		try{
			int hash = Integer.parseInt(code);
			if (hash == deviceid.hashCode()){
				SharedPreferences pref = getSharedPreferences(
						Preferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putBoolean(DLCAppTrial.LICENSED, true);
				editor.commit();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == LICENSE_DIALOG){
			TextView edQuery = (TextView) dialog.findViewById(R.id.edQuery);
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			edQuery.setText(tm.getDeviceId());
		}else
			super.onPrepareDialog(id, dialog);
	}
}
