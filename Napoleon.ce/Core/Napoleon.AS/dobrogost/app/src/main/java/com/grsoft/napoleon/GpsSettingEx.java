package com.grsoft.napoleon;

import android.view.View;

public class GpsSettingEx extends GpsSetting {
	@Override
	protected void init() {
		super.init();
		
		if(!SettingEx.OpenAsAdmin) {
			for(int id : new int[] { R.id.edFrec, R.id.edDist, R.id.cbSendDataInBackground, 
					R.id.spDataSendInterval, R.id.spWaitGPSOnRecieve, R.id.spGPSValidInOrg}) {
				View v = findViewById(id);
				if(v != null)
					v.setEnabled(false);
			}
		}
	}
}
