package com.grsoft.napoleon;

import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
}
