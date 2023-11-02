package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CfgNplEx() {
		monthsToRecreate = 3;
	}

	@Override
	public void resetToDefault() {
		super.resetToDefault();
		monthsToRecreate = 3;
	}
}
