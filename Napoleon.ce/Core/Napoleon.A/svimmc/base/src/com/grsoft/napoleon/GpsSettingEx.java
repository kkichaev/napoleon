package com.grsoft.napoleon;

import android.widget.Spinner;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.CfgNplEx;
import com.grsoft.util.Consts;


public class GpsSettingEx extends GpsSetting {
	Spinner spGPSValidInOrg;
	@Override
	protected int getContentViewID() {	return R.layout.gps_settingex;	}
	
	@Override
	protected void init() {
		super.init();
		
		spGPSValidInOrg = (Spinner) findViewById(R.id.spGPSValidInOrg);
		CfgNplEx config = (CfgNplEx) ConfigManager.getConfig();
		
		spGPSValidInOrg.setSelection(config.gps_valid_in_org / (Consts.SEC_PER_MIN * Consts.ONE_SECOND) - 1, true);
	}
	
	@Override
	protected void updateConfig() {
		int val = CfgNplEx.DEF_VAL_FOR_TIME_GPS_IN_ORG;
		
		CfgNplEx config = (CfgNplEx) ConfigManager.getConfig();
		
		String sel = spGPSValidInOrg.getSelectedItem().toString();
		
		try{
			val = Integer.parseInt(sel) * Consts.SEC_PER_MIN * Consts.ONE_SECOND ;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		config.gps_valid_in_org = val;
	}
}
