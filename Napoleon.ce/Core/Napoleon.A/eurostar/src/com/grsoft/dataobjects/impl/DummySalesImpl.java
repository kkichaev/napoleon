package com.grsoft.dataobjects.impl;


public class DummySalesImpl extends SalesImpl {
	@Override
	public long write() { return -1;}
	
	@Override
	protected boolean checkPriceQty() { return false; }
	
	@Override
	protected void updateQtyPrice(PriceImpl priceImpl, int priceUpdate) {}
}
