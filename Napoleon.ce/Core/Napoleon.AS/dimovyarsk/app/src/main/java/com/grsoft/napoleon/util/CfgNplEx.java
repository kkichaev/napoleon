package com.grsoft.napoleon.util;

import com.grsoft.util.Consts;

public class CfgNplEx extends CfgNpl {

	private static final long serialVersionUID = 1L;

	@Override
	public void resetToDefault() {
		super.resetToDefault();
		
		address = "webapps.v-dymov.ru";
		onlyNewstItems = 1;
		keepAwayInOrder = true;
		gpsFrequience = Consts.ONE_SECOND * 300;
		gpsDistance = 30;
		dataSendInBackground = true;
		gpsSendInterval = 20;
		gps_valid_in_org = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 10;
		priceClmn2Type = 2;
		idInPriceList = true;
		cameraHeight = 480;
		cameraWidth = 640;
	}
}
