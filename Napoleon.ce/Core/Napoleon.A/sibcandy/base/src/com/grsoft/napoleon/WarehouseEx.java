package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected void adapterInit() {
		if(DocType.getCurDoc() == OrderDoc.instance())
			adapter.putFilter(createZeroPositionFilter());
		
		super.adapterInit();
	}
}
