package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
		
		((CheckBox) findViewById(R.id.cbVisit)).setChecked(true);
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);		
	}
}
