package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FoldersAdapter;

import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	final int PERIOD_FOR_DELIVERY = 3;
	final int PERIOD_FOR_ORDER = 1;
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = null;
		
		if( DocType.getCurDoc() == ReturnDoc.instance())
			ret = createAssortementMatrixAdapter();
		else 
			ret = (FoldersAdapter) super.createListAdapter();
		
		return ret;
	}

	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (DocType.getCurDoc() == ReturnDoc.instance()) {
			AssortmentMatrixAdapter.MATRIX_DOC = DeliveryDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_DELIVERY;
		}else {
			AssortmentMatrixAdapter.MATRIX_DOC = OrderDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_ORDER;
		}
		
		return super.createAssortementMatrixAdapter();
	}
}
