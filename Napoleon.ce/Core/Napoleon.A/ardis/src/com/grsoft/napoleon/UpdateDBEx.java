package com.grsoft.napoleon;

import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		((CheckBox)findViewById(R.id.cbDebt)).setChecked(true);
		((CheckBox)findViewById(R.id.cbVisit)).setChecked(true);
	}
}
