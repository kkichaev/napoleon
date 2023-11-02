package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;
	
	@Scale(value = 10)
	public int dscQuant = 0;
	
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();
}
