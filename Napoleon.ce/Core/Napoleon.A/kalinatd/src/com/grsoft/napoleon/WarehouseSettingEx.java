package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;

import android.widget.CheckBox;

public class WarehouseSettingEx extends WarehouseSetting {
	CheckBox cbOrdSumEx;
	
	@Override protected int getContentViewID() { return R.layout.warehouse_setting_new_ex;	}
	
	@Override
	protected void init() {
		super.init();
		
		cbOrdSumEx = (CheckBox) findViewById(R.id.cbOrdSumEx);
		
		CfgNplEx cfex = (CfgNplEx)config;
		cbOrdSumEx.setChecked(cfex.ordSumEx);
	}
	
	@Override
	public void save() {
		CfgNplEx cfex = (CfgNplEx)config;
		cfex.ordSumEx = cbOrdSumEx.isChecked();
		
		super.save();
	}
}
