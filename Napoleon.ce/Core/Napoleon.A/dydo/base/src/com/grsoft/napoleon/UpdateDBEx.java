package com.grsoft.napoleon;

import android.view.View;


public class UpdateDBEx extends UpdateDB {
	@Override
	protected int getContentView() { return R.layout.updatedbex; }
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
		findViewById(R.id.cbDebt).setVisibility(View.GONE);
		findViewById(R.id.cbPresent).setVisibility(View.GONE);
	}
}
