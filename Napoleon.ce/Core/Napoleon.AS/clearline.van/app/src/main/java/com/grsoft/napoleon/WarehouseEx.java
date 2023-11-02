package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.widget.BaseAdapter;

public class WarehouseEx extends Warehouse {
	
	PriceImpl pi = new PriceImpl();
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if(document instanceof SalesImpl || document instanceof OrderImplEx)
			ret.putFilter(new ZeroCostFilter(document.getId(), CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass())));
		return ret;
	}

	@Override
	protected Filter createZeroPositionFilter() {
		return new ZPF();
	}
	
	class ZeroCostFilter extends Filter {
		static final String NAME = "ZERO_COST"; 
		
		CostStrategy costStrategy;
		public ZeroCostFilter(String id, CostStrategy cs) {
			super(NAME + id);
			costStrategy = cs;
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			pi.read(priceRowID);
			long cost = costStrategy.getItemCost(pi.getData(), document);
			return cost > 0;
		}
		
	}
}

class ZPF extends ZeroPositionFilter {
	public ZPF() {
		if(DocType.getCurDoc() == WSOrderDoc.instance())
			where = "vanQty>0";
	}
}


