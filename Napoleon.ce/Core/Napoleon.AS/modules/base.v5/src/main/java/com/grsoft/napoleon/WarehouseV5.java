package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;

public class WarehouseV5 extends Warehouse {
	static final int PERIOD_FOR_DELIVERY = 12;
	static final int PERIOD_FOR_ORDER = 1;
	public static final String COST_FILTER = "cost_filter";
	
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
