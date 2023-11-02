package com.grsoft.napoleon.util;

import com.grsoft.napoleon.util.CfgNpl;

public class CfgNplEx extends CfgNpl {
	private static final long serialVersionUID = 1L;
	
	public boolean roundPrice = false;
	
	@Override
	public void resetToDefault(){
		super.resetToDefault();
		
		roundPrice = false;
	}

}
