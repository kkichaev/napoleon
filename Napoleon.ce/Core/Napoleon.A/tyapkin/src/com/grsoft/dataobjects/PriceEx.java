package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends PricePrint {
	@Scale(value=Consts.SUM_SCALE)
	public int mincost;
}
