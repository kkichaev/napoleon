package com.grsoft.util;

import com.grsoft.napoleon.util.CfgNpl;

public class CfgNplEx extends CfgNpl{
	private static final long serialVersionUID = 1L;
	public int unfire_rest = 24; 
	
	public String encoding = "windows-1251";
	
	/***
	 * Время в милисекундах когда 
	 * не будет спашивать GPS для документа в одной и той же 
	 * организации
	 */
	public static final int DEF_VAL_FOR_TIME_GPS_IN_ORG = 5 * Consts.ONE_SECOND * Consts.SEC_PER_MIN ; 
	public int gps_valid_in_org = DEF_VAL_FOR_TIME_GPS_IN_ORG;
}
