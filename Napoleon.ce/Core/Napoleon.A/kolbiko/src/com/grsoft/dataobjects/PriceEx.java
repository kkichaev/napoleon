package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value = Consts.WEIGHT_SCALE)
	public int avgWeight;
}
