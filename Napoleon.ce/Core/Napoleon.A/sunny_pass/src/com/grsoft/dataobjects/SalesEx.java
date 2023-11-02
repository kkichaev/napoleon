package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class SalesEx extends Sales {
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
