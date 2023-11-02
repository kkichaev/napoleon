package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.QTY_SCALE)
	public int minQty = 0;
	
	public int nodisc = 0;
}
