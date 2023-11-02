package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;

import android.widget.CheckBox;

public class WarehouseSettingEx extends WarehouseSetting {
	@Override
	protected int getContentViewID() { return R.layout.warehouse_settingex; }


	@Override
	protected void init() {
		super.init();
		
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbPackInput);
		cb.setChecked(((CfgNplEx)config).packInput);
	}
	
	@Override
	public void save() {
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbPackInput);
		((CfgNplEx)config).packInput = cb.isChecked();
		
		super.save();
	}
}
