package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class SalesEx extends Sales implements IOrder{
	@Scale(value=Consts.SUM_SCALE)
	public int discOrd;

	@Override
	public int getDisc() {
		return discOrd;
	}

	@Override
	public void setDisc(int val) {
		discOrd = val;
	}
}
