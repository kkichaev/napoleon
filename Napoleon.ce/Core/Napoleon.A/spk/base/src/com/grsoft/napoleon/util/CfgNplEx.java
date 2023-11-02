package com.grsoft.napoleon.util;


public class CfgNplEx extends CfgNpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean automatic_report_request = false;
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		
		address = "192.168.104.194";
		port = 7777;
	}
}
