package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((CheckBox)findViewById(R.id.cbDebt)).setChecked(true);
	}
	
	@Override
	protected void postSync(Boolean result) {
		if(result){
			CostStrategyEx.refreshCash();
		}
	}
}
