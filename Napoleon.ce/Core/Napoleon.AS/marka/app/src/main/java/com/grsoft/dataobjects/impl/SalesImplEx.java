package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;

public class SalesImplEx extends SalesImpl {
//	@Override
//	public boolean delete() {
//		if( isExported() )
//			return true;
//		return super.delete();
//	}

	@Override protected boolean checkPriceQty() { return false; }

//	@Override
//	protected int getUpdateQtyValue(Price price) {
//		int qty = super.getUpdateQtyValue(price);
//		if( (qty % Consts.QTY_SCALE) != 0 ) {
//			qty = ((qty / Consts.QTY_SCALE) + 1) * Consts.QTY_SCALE;
//		}
//		return qty;
//	}
}
