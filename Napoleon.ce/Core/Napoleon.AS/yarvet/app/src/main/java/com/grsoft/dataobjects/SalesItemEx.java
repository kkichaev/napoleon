package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class SalesItemEx extends SalesItem implements IOrderItem{
	public int sumTypeItem;
	@Scale(value=Consts.SUM_SCALE)
	public int discItem;
	
	@Override
	public int getSumType() {
		return sumTypeItem;
	}
	@Override
	public void setSumType(int val) {
		sumTypeItem = val;
		
	}
	@Override
	public int getDisc() {
		return discItem;
	}
	@Override
	public void setDisc(int val) {
		discItem = val;
	}
	@Override
	public int getCost() {
		return cost;
	}
}
