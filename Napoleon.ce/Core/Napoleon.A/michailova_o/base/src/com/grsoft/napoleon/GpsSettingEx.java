package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import android.widget.CheckBox;

public class GpsSettingEx extends GpsSetting {
	@Override protected int getContentViewID() { return R.layout.gps_settingex; }
	
	@Override
	protected void init() {
		super.init();
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		CheckBox cb = (CheckBox) findViewById(R.id.cbComplexSalesHistory);
		
		if(cb != null)
			cb.setChecked(config.isComplexSalesHistory);
	}
	
	@Override
	protected void updateConfig() {
		super.updateConfig();
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		
		CheckBox cb = (CheckBox) findViewById(R.id.cbComplexSalesHistory);
		
		if(cb != null)
			config.isComplexSalesHistory = cb.isChecked();
	}
}
