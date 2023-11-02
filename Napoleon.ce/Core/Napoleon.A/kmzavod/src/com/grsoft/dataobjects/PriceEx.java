package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.QTY_SCALE)
	public int quant = 0;
	public int type = 0;
	public int godn = 0;
	
}
