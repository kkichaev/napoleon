package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends OrderPrint implements IOrder{
	@Scale(value=Consts.SUM_SCALE)
	public int discOrd;

	public int fromKIS = 0;
	
	@Override
	public int getDisc() {
		return discOrd;
	}

	@Override
	public void setDisc(int val) {
		discOrd = val;
	}
}
