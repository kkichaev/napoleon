package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;

import android.widget.CheckBox;

public class WarehouseSettingEx extends WarehouseSetting {
	@Override protected int getContentViewID() { return R.layout.warehouse_setting_ex; }
	
	@Override
	protected void init() {
		super.init();
		
		CheckBox cb = (CheckBox) findViewById(R.id.inputInPack);
		cb.setChecked(((CfgNplEx)config).packInput);
	}
	
	@Override
	public void save() {
		CheckBox cb = (CheckBox) findViewById(R.id.inputInPack);
		if( cb != null )
			((CfgNplEx)config).packInput = cb.isChecked();

		super.save();
	}
}
