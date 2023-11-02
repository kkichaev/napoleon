package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.util.Consts;

public class SalesImplEx extends SalesImpl {
	@Override
	public boolean delete() {
		if( isExported() )
			return true;
		return super.delete();
	}
	
	@Override
	public void postInit() {
		super.postInit();
		((SalesEx)data).isBlack = 1;
	}

	@Override
	protected int getUpdateQtyValue(PricePrint price) {
		int qty = super.getUpdateQtyValue(price);
		if( (qty % Consts.QTY_SCALE) != 0 ) {
			qty = ((qty / Consts.QTY_SCALE) + 1) * Consts.QTY_SCALE;
		}
		return qty;
	}

	public void refreshTax() {
		PriceImpl pi = new PriceImpl();
		PricePrint pp = (PricePrint) pi.getData();
		
		for(OrderItem oi : data.items) {
			pp.id = oi.id;
			pi.read();
			((SalesItem)oi).countTax(data, pp.tax1);
		}
		
		pi.close();
		
	}
}
