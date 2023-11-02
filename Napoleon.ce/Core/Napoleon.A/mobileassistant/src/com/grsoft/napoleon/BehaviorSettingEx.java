package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;


public class BehaviorSettingEx extends BehaviorSetting {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cbVisitToDel.setVisibility(View.GONE);
		spVisitToDel.setVisibility(View.GONE);
	}
}
