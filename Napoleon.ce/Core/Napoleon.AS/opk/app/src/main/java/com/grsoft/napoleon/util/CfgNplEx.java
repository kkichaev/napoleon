package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {

	private static final long serialVersionUID = 1L;

	public String notifySound = "";
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		notifySound = "";
	}
}
