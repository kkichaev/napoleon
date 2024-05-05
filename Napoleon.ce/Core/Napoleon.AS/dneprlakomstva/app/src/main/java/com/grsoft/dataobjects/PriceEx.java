package com.grsoft.dataobjects;


import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public int changes;
	public int boxed;
	
	public String priceGroup = "";

	@Scale(value = Consts.SUM_SCALE)
	public int minCost = 0;
}
