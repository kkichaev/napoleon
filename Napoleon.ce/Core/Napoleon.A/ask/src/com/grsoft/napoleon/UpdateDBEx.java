package com.grsoft.napoleon;

import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		((CheckBox) findViewById(R.id.cbCost)).setChecked(true);
	}
	
	@Override
	protected void postSync(Boolean result) {
		if(result){
			CostStrategyEx.refreshCash();
		}
	}
}
