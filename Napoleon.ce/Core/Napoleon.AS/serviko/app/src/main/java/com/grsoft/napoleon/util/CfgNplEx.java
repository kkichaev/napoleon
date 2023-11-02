package com.grsoft.napoleon.util;

import com.grsoft.util.Consts;

public class CfgNplEx extends CfgNpl {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		
		gpsDistance = 150;
		gpsFrequience = Consts.ONE_SECOND * 60;
		dataSendInBackground = true;
		waitGpsCoordOnRequest = 30;
		gps_valid_in_org = 20 * Consts.ONE_SECOND * Consts.SEC_PER_MIN ;
		isAutostart = true;
		isService = true;
		address = "87.229.251.14";
		address2 = "185.242.119.50";
	}

}
