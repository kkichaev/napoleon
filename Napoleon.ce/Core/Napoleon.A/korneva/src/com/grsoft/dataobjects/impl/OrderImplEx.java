package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderProceededEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public boolean isEditable() {
		return (!isExported() || 
				(isProceeded() && ((data.params & OrderProceededEx.APPROVED) ==  0)));
	}
}
