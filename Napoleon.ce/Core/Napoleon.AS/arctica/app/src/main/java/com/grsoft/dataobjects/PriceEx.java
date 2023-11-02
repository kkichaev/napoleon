package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.SUM_SCALE)
	public int minCost;
	
	public int canDiscount;
	
	public String unit = "";
	public List<PriceUnit> units = new ArrayList<PriceUnit>();
}
