package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.widget.BaseAdapter;


public class WarehouseEx extends Warehouse {
	
	static String idOrg = "";
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter(); 
		DocType ct = DocType.getCurDoc(); 
		if(ct == SalesDoc.instance() || ct == OrderDoc.instance() && document != null) {
			String orgId = document.getId(); 
			if( idOrg != orgId ) {
				FoldersAdapter.resetCache();
				idOrg = orgId;
			}
			ret.putFilter(createZeroPositionFilter());
		}
		return ret;
	}
	
	public static void resetCache() { idStore = ""; }

	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new ZeroFilter((OrderImplEx)document);
		}
		return document instanceof SalesImpl ? new ZeroPositionFilter(document, price) : super.createZeroPositionFilter();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		CostStrategy costStrategy;
		OrderImplEx document;
		
		public ZeroFilter(OrderImplEx doc) { 
			this.document = doc;
			costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		}
		
		@Override public String getWhereStr() { return ""; }

		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false; 
			
			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImplEx)document).getItemValue(p) > 0);
			if (result )
				result = (costStrategy.getItemCost(p, document) > 0);
			return result;
		}
	}
}
