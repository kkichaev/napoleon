package com.grsoft.napoleon;

import android.widget.CheckBox;

import com.grsoft.napoleon.util.CfgNplEx;

public class WarehouseSettingEx extends WarehouseSetting {
	
	@Override
	protected int getContentViewID() { return R.layout.warehouse_setting_ex; }
	
	@Override
	protected void init() {
		super.init();
		
		CheckBox cb = (CheckBox) findViewById(R.id.cbRoundPrice);
		cb.setChecked(((CfgNplEx)config).roundPrice);
	}
	
	@Override
	public void save() {
		CheckBox cb = (CheckBox) findViewById(R.id.cbRoundPrice);
		((CfgNplEx)config).roundPrice = cb.isChecked();
		
		super.save();
	}
}
