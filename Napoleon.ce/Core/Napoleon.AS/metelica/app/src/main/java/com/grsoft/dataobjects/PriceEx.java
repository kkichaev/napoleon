package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public String barcode = "";
	
	public int best = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int minCost = 0;

	@Scale(value= Consts.SUM_SCALE)
	public int mgrCost = 0;

	@Scale(value= Consts.QTY_SCALE)
	public int limit = 0;
}
