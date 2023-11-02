package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RemnantItemEx extends RemnantItem {
	@Scale(value=Consts.QTY_SCALE)
	public int qtyBoard;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qtyWh;
}
