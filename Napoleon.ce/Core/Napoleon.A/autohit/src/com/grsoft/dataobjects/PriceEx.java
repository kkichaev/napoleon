package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.SUM_SCALE)
	public int minCost = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int minQty = 0;
	
	public String article;
	public List<UnitItem> units = new ArrayList<UnitItem>();

}
