package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.documents.CreatableDocument;

public class SalesImplEx extends SalesImpl {
	@Override
	public boolean isEditable() {
		if( ((SalesEx)data).schfNumber.length() != 0 )
			return false;
		return super.isEditable();
	}
	
	
	@Override
	protected void postCopyProcess(CreatableDocument<Sales> copy) {
		super.postCopyProcess(copy);
		((SalesEx)copy.getData()).schfNumber = "";
	}
}
