package com.grsoft.napoleon;

import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cb;
		cb = (CheckBox) findViewById(R.id.cbRemains);
		cb.setChecked(false);
		
		cb = (CheckBox) findViewById(R.id.cbDebt);
		cb.setChecked(true);
	}
}
