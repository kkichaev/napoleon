package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehosueEx extends WarehouseNew {
	static int whIndex = 0;

	@Override
	protected Filter createZeroPositionFilter() {
		if( document instanceof OrderImplEx ) {
			if( whIndex != ((OrderEx)document.getData()).whNumber ) {
				whIndex = ((OrderEx)document.getData()).whNumber;
				FoldersAdapter.resetCache();
			}
		} else if( whIndex != 0 ) {
			whIndex = 0;
			FoldersAdapter.resetCache();			
		}
		
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
