package com.grsoft.napoleon;

import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDBPrint {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
	}
}
