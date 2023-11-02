package com.grsoft.napoleon;

import android.view.View;

public class BehaviorSettingEx extends BehaviorSetting {
	@Override
	protected void init() {
		super.init();

		if(!SettingEx.OpenAsAdmin) {
			for(int id : new int[] {R.id.cbAutostart, R.id.cbVisitToDel, R.id.spVisitToDel, R.id.cbAutostartAsService }) {
				View v = findViewById(id);
				if(v != null)
					v.setEnabled(false);
			}
		}
	}
}
