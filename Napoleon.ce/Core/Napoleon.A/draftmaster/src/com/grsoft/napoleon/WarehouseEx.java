package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.ExtrasConst;


public class WarehouseEx extends WarehouseNew {
	
	@Override
	protected void postAdapterInit() {
		if(document.getRowid() != ExtrasConst.INVALID_ROWID && AssortmentMatrixAdapter.hasAssortiment(document.getId()))
			applayMatrix(AssortmentMatrixAdapter.TITLE);
		else
			super.postAdapterInit();
	}
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		AssortmentMatrixAdapter.MATRIX_DOC = DocType.getCurDoc();
		return super.createAssortementMatrixAdapter();
	}
}
