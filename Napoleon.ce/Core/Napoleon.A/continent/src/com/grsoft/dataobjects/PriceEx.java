package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends PricePrint {
	public int canChangeCost;
	@Scale(value=Consts.QTY_SCALE)
	public int minQty = 0;
	public String info = "";
	public int packOnly = 0;
}
