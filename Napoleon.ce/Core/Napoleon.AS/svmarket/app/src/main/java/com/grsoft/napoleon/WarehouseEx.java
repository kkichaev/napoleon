package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends Warehouse {
	PriceImpl price = new PriceImpl();

	static int skladId = 0;

	@Override
	protected BaseAdapter createListAdapter() {
		int cs = (document instanceof OrderImplEx) ? ((Order)document.getData()).supplyer : 0;
		if(skladId != cs) {
			FoldersAdapter.resetCache();
			skladId = cs;
		}
		return super.createListAdapter();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		price.close();
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 
			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
}
