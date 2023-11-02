package com.grsoft.napoleon.util;

import com.grsoft.util.Consts;

public class CfgNplEx extends CfgNpl {

	private static final long serialVersionUID = 1L;

	@Override
	public void resetToDefault() {
		super.resetToDefault();
		
		setDefaults();
	}
	
	public void setDefaults() {
		dataSendInBackground = true;
		gpsDistance = 100;
		gpsFrequience = Consts.ONE_SECOND * 60;
		gpsSendInterval = 10;
		waitGpsCoordOnRequest = 60;
		gps_valid_in_org = 5 * Consts.SEC_PER_MIN * Consts.ONE_SECOND ;
		
		isAutostart = true;
		isService = true;
		day_to_del_visit = 30; 
	}
}
