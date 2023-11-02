package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {

	private static final long serialVersionUID = 1L;

	public CfgNplEx() {
		resetToDefault();
	}
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();

		dataSendInBackground = true;
		onlyNewstItems = 1;
		isPackView = false;
		monthsToRecreate = 3;
		day_to_del_visit = 30;
		keepAwayInOrder = true;
		port = 8889;
		checkPrice = true;
	}
}
