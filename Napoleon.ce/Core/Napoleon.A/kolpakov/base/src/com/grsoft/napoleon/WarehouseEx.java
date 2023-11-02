package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter adapter = (FoldersAdapter) super.createListAdapter();
		if(document instanceof OrderImpl) {
			OrderEx oe = (OrderEx)document.getData();
			adapter.putFilter(new StoreFilter(oe.storeid));
		} else if(document instanceof SalesImplEx) {
			SalesEx oe = (SalesEx)document.getData();
			adapter.putFilter(new StoreFilter(oe.storeid));
		}
		return adapter;
	}
}

class StoreFilter extends Filter {
	
	public StoreFilter(String storeId) {
		super("Store" + storeId);
		
		where = "storeid='" + storeId + "'";
	}
}