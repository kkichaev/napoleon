package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class SalesItemEx extends SalesItem 
implements DocItemEx{
	@FieldOrder(order=9)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;

	@Override
	public int getDiscount() {
		return discount;
	}

	@Override
	public void setDiscount(int val) {
		discount = val;
	}

	@Override
	public int getCost() {
		return cost;
	}
}
