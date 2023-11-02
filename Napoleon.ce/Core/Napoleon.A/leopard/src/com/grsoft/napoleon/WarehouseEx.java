package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected Filter createZeroPositionFilter() {
		return new Filter(ZeroPositionFilter.NAME) {
			@Override
			public boolean inset(long priceRowID, String id) {
				boolean result = false; 
				PriceImpl price = new PriceImpl();
				if(price.read(priceRowID))
					result = ((Itemsable)document)
						.getItemValue(price.getData()) > 0;
				price.close();
				
				return result;
			}
		};
	}
}
