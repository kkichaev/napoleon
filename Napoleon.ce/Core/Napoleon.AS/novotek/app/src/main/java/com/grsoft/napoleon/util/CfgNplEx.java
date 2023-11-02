package com.grsoft.napoleon.util;

import com.grsoft.napoleon.BuildConfig;

public class CfgNplEx extends CfgNpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CfgNplEx() {
		address="mx.novo-tek.net";
		if(BuildConfig.FLAVOR.equals("avix")) {
			port = 1132;
		} else if(BuildConfig.FLAVOR.equals("trade")) {
			port = 1134;
		} else {
			port = 1130;
		}
		vibration = true;
		allowRotateScreen = true;
		isAutostart = true;
		isService = true;
		
		checkPrice = true;
	}
}
