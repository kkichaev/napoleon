package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {

	private static final long serialVersionUID = 1L;

	public String newReturnSound = "";
	public int newReturnAlarm = 0;
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		newReturnSound = "";
		newReturnAlarm = 0;
	}
}
