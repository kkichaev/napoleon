package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.AssortmentMatrixAdapter;


public class WarehouseEx extends WarehouseNew {
	protected void postAdapterInit() {
		if(DocType.getCurDoc() == OrderDoc.instance())
			applayMatrix(AssortmentMatrixAdapter.TITLE);
		else
			adapter.buildSet(folderID);
	}
}
