package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
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
		document.read(docRowId, false);
		String id = Integer.toString(document.getSumType());
		ret.putFilter(new ZeroCostFilter(id));
		return ret;
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilterEx();
	}
	
	class ZeroCostFilter extends Filter {
		CostStrategy cs;
		
		@SuppressWarnings("unchecked")
		public ZeroCostFilter(String id) { 
			super(id);
			cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean ret = false;
			if( filterData.read(priceRowID) )
				ret = (cs.getItemCost(filterData.getData(), document) != 0);
			return ret;
		}
	}
	
	class ZeroFilterEx extends ZeroPositionFilter {

		@Override
		public String getWhereStr() {
			if( document instanceof OrderImplEx ) {
				OrderEx oe = (OrderEx)document.getData();
				if( oe.whIndex != 0 )
					return "";
			}
			return super.getWhereStr();
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( document instanceof OrderImplEx ) {
				if( filterData.read(priceRowID) )
					return (((OrderImplEx)document).getItemValue(filterData.getData()) > 0);
			}
			return super.inset(priceRowID, id);
		}
	}
}
