package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BehaviorSettingEx extends BehaviorSetting {

	Uri uri;
	
	protected int getContentViewID() {
		return R.layout.behavior_settingex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button setup_notification_sound;
		
		setup_notification_sound=(Button) findViewById(R.id.setup_notification_sound);
		
		
		setup_notification_sound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
				startActivityForResult(intent, 115);
				
		
			}
		});
	}
	
	@Override
	public void save() {
		super.save();
		CfgNplEx cfex = (CfgNplEx) config;
		cfex.notifySound = uri == null ? "" : uri.toString();
		
	}
		 @Override
		 protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent){
			     if (resultCode == Activity.RESULT_OK && requestCode == 115){
			    	uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		     	}
		 }
		 
}
