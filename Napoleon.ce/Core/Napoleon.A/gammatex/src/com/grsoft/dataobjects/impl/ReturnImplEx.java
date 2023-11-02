package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.napoleon.documents.DebtDoc;


public class ReturnImplEx extends ReturnImpl {
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		((PricePrint)price.getData()).vanQty += -qty;
		price.write();
		DebtDoc.instance().refreshDocSum(data.id);
	}
	
	@Override
	public int getItemValue(Price item) {
		return ((PricePrint)item).vanQty;
	}
	
	@Override
	public long sum() {
		return -super.sum();
	}
	
	@Override
	protected boolean checkPriceQty() {
		return true;
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		return qty;
	}
	

}
