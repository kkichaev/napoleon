package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;

public class BehaviorSettingEx extends BehaviorSetting {
	@Override protected int getContentViewID() { return R.layout.behavior_ex; }

	@Override
	protected void init() {
		super.init();
		
		CfgNplEx cfg = (CfgNplEx) config;
		((CheckBox) findViewById(R.id.cbReturnSound)).setChecked(cfg.newReturnAlarm > 0);
		
		findViewById(R.id.btnReturnSound).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
				startActivityForResult(intent, 115);
			}
		});
	}

	 @Override
	 protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent){
		     if (resultCode == Activity.RESULT_OK && requestCode == 115){
		    	 Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		    	((CfgNplEx) config).newReturnSound = uri.toString();
		    	((CheckBox) findViewById(R.id.cbReturnSound)).setChecked(true);
	     	}
	 }
	 
	 @Override
	public void save() {
		CfgNplEx cfg = (CfgNplEx) config;
		if(((CheckBox) findViewById(R.id.cbReturnSound)).isChecked()) {
			cfg.newReturnAlarm = 1;
		} else {
			cfg.newReturnAlarm = 0;
		}
		super.save();
	}
}
