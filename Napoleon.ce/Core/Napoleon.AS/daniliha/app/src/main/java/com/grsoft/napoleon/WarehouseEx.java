package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends Warehouse {
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter ret = super.createAdapterInstance();
		if(document != null) {
			ret.putFilter(new ZeroCostFilter(document.getId(), document.getDate()));
		}
		return ret;
	}
	
	class ZeroCostFilter extends Filter {
		static final String NAME = "ZERO_COST"; 
		Date date;
		String orgId;
		
		CostStrategy costStrategy;
		public ZeroCostFilter(String id, Date date) {
			super(NAME + id);
			this.date = date;
			this.orgId = id;
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			int cost = CostStrategyEx.getCost(id, orgId, date);
			return cost > 0;
		}
	}
}
