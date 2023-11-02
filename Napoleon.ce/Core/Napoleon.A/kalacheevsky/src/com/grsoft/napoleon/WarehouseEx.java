package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Filter;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	PriceImpl filterData = new PriceImpl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStop() {
		filterData.close();
		super.onStop();
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		WarehouseAdapter ret = (WarehouseAdapter)super.createListAdapter(); 
		ret.putFilter(new ZeroCostFilter());
		return ret;
	}
	
	@Override
	protected void updateForZeroFilter() {
		if (adapter.getFilter(ZeroPositionFilter.NAME) == null)
			adapter.putFilter(new ZeroCostFilter());
		else
			adapter.deleteFilter(ZeroPositionFilter.NAME);
		
		adapter.buildSet();
	}
	
	class ZeroCostFilter extends Filter {
		CostStrategy cs;
		
		@Override
		public String getName() { return ZeroPositionFilter.NAME; }
		
		@SuppressWarnings("unchecked")
		public ZeroCostFilter() {
			super("");
			cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		}
		
		@Override
		public boolean inset(long priceRowID) {
			boolean ret = false;
			if( filterData.read(priceRowID) )
				ret = (cs.getItemCost(filterData.getData(), document) != 0);
			return ret;
		}
	}
}
