package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

public class WarehouseEx extends WarehouseNew {
	boolean presentInited = false;
	
	@Override
	protected void fireBuildSet() {
		super.fireBuildSet();
		
		if(!presentInited && 
				DocType.getCurDoc() == OrderDoc.instance()){
			presentInited = true;
			openPresentation();
			finish();
		}
	}
}
