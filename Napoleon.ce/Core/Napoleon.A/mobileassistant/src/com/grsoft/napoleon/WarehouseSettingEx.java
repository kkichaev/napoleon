package com.grsoft.napoleon;

import android.view.View;


public class WarehouseSettingEx extends WarehouseSetting {
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.checkPrice).setVisibility(View.GONE);
		findViewById(R.id.cbComplexSalesHistory).setVisibility(View.GONE);
	};
}
