package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {
	private static final long serialVersionUID = 1L;

	public CfgNplEx() {
		isPackView = true;
		onlyNewstItems = 1;
	}
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		isPackView = true;
		onlyNewstItems = 1;
	}
}
