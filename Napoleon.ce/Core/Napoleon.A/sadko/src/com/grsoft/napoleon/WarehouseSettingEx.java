package com.grsoft.napoleon;

import android.widget.CheckBox;

import com.grsoft.napoleon.util.CfgNplEx;

public class WarehouseSettingEx extends WarehouseSetting {
	@Override protected int getContentViewID() { return R.layout.warehouse_setting_ex;	}

	@Override
	protected void init() {
		super.init();
		
		CfgNplEx cfex = (CfgNplEx)config;
		((CheckBox) findViewById(R.id.cbSuppl)).setChecked(cfex.showSupplier);
	}

	@Override
	public void save() {
		CfgNplEx cfex = (CfgNplEx)config;
		cfex.showSupplier = ((CheckBox) findViewById(R.id.cbSuppl)).isChecked();
		
		super.save();
	}
}
