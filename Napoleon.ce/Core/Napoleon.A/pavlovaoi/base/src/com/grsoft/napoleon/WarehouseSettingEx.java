package com.grsoft.napoleon;

import android.view.View;

public class WarehouseSettingEx extends WarehouseSetting {
	
	@Override
	protected void initUpdatePriceControls() {
		super.initUpdatePriceControls();
		
		findViewById(R.id.checkPrice).setEnabled(false);
		findViewById(R.id.cbUseUpdatePrice).setEnabled(false);
		findViewById(R.id.spUpdatePriceInBg).setEnabled(false);
	}
}
