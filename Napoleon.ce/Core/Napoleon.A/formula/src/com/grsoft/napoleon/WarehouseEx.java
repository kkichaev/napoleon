package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;

public class WarehouseEx extends WarehouseNew {

	@Override
	protected void postAdapterInit() {
		super.postAdapterInit();
		
		if(document.getRowid() != ExtrasConst.INVALID_ROWID && 
				DocType.getCurDoc() == OrderDoc.instance() && 
				((OrderEx)document.getData()).fact == 0)
			adapter.putFilter(new NotMercuryFilter());
			
	}
	private static class NotMercuryFilter  extends Filter{
		private PriceImpl price = new PriceImpl();
		
		public NotMercuryFilter() {
			super("MercuryFilter");
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			price.read(priceRowID);
			
			return ((PriceEx)price.getData()).mercury == 0;
		}
	}
}
