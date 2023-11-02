package com.grsoft.ads.utils;

import com.grsoft.napoleon.util.Config;
import com.grsoft.util.Consts;

public class CfgAds extends Config {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void resetToDefault() {
		super.resetToDefault();

		gpsDistance = 150;
		gpsFrequience = Consts.ONE_SECOND * 30;
	}
}
