package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class CostData {
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;

	@Scale(value=Consts.SUM_SCALE)
	public int minCost = 0;
}
