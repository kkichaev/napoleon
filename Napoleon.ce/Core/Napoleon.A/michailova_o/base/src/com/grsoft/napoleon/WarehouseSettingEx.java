package com.grsoft.napoleon;

import android.view.View;

public class WarehouseSettingEx extends WarehouseSetting {
	
	@Override
	protected void init() {
		super.init();
		
		View v = findViewById(R.id.cbComplexSalesHistory);
		
		if(v != null)
			v.setVisibility(View.GONE);
	}
}
