package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.BaseAdapter;


public class WarehouseEx extends WarehouseNew {
	PriceImpl pi = new PriceImpl();
	private static final String PRICE_INITED = "price_inited";
	
	public boolean isPriceExpand() {
		boolean pi = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getBoolean(PRICE_INITED, false);
		
		if(!pi){
			SharedPreferences p = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
			Editor ed = p.edit();
			ed.putBoolean(PRICE_INITED, true);
			ed.commit();
			
			ed = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
			ed.putBoolean(EXPAND_PRICE_PREF, true);
			ed.commit();
		}
		
		return super.isPriceExpand();
	};
	
	@SuppressWarnings("unchecked")
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if(document instanceof OrderImpl)
			ret.putFilter(new ZeroCostFilter(document.getId(), CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass())));
		return ret;
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
			int cost = costStrategy.getItemCost(pi.getData(), document);
			return cost > 0;
		}
		
	}
}
