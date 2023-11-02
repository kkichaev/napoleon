package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {
	private static final long serialVersionUID = 1L;
	public boolean simpleMode = false;
	
	public CfgNplEx() {
//		resetToDefault();
	}
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();

		address = "195.128.59.34";
		address2 = "192.168.8.26";
		
		vibration = true;
		allowRotateScreen = true;
		
		isService = true;
		isAutostart = true;
		
		monthsToRecreate = 6;

		day_to_del_visit = 0;
		dataSendInBackground = true;
		
		waitGpsCoordOnRequest = 20;
		simpleMode = false;
	}
}
