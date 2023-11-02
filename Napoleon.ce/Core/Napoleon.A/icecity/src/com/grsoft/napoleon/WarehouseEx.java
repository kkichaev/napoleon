package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
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
		@Override
		public String getName() { return ZeroPositionFilter.NAME; }
		
		public ZeroCostFilter() {
			super("");
		}
		
		@Override
		public boolean inset(long priceRowID) {
			boolean ret = false;
			if( document instanceof OrderImplEx){ 
				if (filterData.read(priceRowID))
					ret = ((OrderImplEx)document).getItemValue(filterData.getData()) > 0;
			} else
				ret = false;
				
			return ret;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(document instanceof OrderImplEx)
			((OrderImplEx)document).resetSklad();
	}

}
