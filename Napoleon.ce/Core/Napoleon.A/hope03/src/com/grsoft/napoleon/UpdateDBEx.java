package com.grsoft.napoleon;

import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbVisit = (CheckBox) findViewById(R.id.cbVisit);
		cbVisit.setChecked(true);
	}
}
