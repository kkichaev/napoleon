package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public List<PriceQtyItem> whQty;
	
	@Scale(value=100000)
	public int cubature = 0;

	@Scale(value=Consts.SUM_SCALE)
	public int minCost = 0;
}