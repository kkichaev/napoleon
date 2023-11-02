package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;


public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {	
		Price price = priceImpl.getData();
		RemnantItem item = (RemnantItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ){
			item = new RemnantItem();
			item.id = price.id;
			item.qty = qty;
			data.items.add(item);
		} else {
			if( item.qty != qty )
				item.qty = qty;
			else
				needUpdate = false;
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}
	
	public boolean deleteItem(String id) {
		boolean result = false;
		DataObject item = findItem(id);
		
		if(item != null){
			data.items.remove(item);
			result = true;
		}
		
		return result;
	}
}
