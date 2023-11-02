package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;

import android.widget.CheckBox;

public class WarehouseSettingEx extends WarehouseSetting {
	@Override protected int getContentViewID() { return R.layout.warehouse_setting_ex; }

	@Override
	protected void init() {
		super.init();
		
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbShowNewMatrix);
		cb.setChecked(((CfgNplEx)config).showNewMatrix);
	}
	
	@Override
	public void save() {
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbShowNewMatrix);
		((CfgNplEx)config).showNewMatrix = cb.isChecked();
		
		super.save();
	}
}
