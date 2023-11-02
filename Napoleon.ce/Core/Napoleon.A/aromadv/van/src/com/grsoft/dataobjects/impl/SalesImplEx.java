package com.grsoft.dataobjects.impl;

public class SalesImplEx extends SalesImpl {
	@Override
	public boolean delete() {
		if( data.items != null && data.items.size() > 0 )
			return true;
		return super.delete();
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		if( !isEditable() )
			return true;
		// нельзя удалить последнюю позицию
		if( data.items != null && data.items.size() == 1 && qty == 0 )
			return true;
		return super.updateQty(priceImpl, qty, cost, inPack);
	}
}
