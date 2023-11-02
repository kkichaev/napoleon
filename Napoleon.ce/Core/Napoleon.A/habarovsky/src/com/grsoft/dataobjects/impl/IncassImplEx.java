package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.IncassEx;


public class IncassImplEx extends IncassImpl {
	@Override
	public boolean isEditable() {
		boolean result = super.isEditable();
		
		if(result)
			result = (data.params & IncassEx.SAVED) != IncassEx.SAVED;
		
		return result;
	}
}
