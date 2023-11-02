package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.SUM_SCALE)
	public int minCost;
	
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();

}
