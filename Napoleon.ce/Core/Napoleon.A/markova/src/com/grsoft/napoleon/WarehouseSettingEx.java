package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;
import android.widget.Spinner;

public class WarehouseSettingEx extends WarehouseSetting {
	Spinner spMonth;
	@Override
	protected void init() {
		super.init();
		
		spMonth = (Spinner) findViewById(R.id.spMonth);
		CfgNplEx ce = (CfgNplEx) config;
		
		spMonth.setSelection(ce.selsail - 1, true);
	}
	
	@Override protected int getContentViewID() { return R.layout.warehouse_settingex; }
	
	@Override
	public void save() {
		CfgNplEx ce = (CfgNplEx) config;
		ce.selsail = spMonth.getSelectedItemPosition() + 1;
		
		super.save();
	}
}
