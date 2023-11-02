package com.grsoft.napoleon.utl;

import com.grsoft.napoleon.util.CfgNpl;

public class CfgNplEx extends CfgNpl {
	private static final long serialVersionUID = 1L;

	public CfgNplEx() {
		resetToDefault();
	}
	
	@Override
	public void resetToDefault() {
		super.resetToDefault();
		
		isAutostart = true;
		isService = true;
		dataSendInBackground = true;
		gpsSendInterval = 10;
	}
}
