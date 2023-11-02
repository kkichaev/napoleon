package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public int isWeight;
	public int expiration = 0;

	@Scale(value=Consts.QTY_SCALE)
	public int minQty;
}
