package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int due;
	@Scale(value=Consts.SUM_SCALE)
	public int postdue;
	@Scale(value=Consts.SUM_SCALE)
	public int limitsum;
	public int limitcnt;
	
	@Scale(value=Consts.SUM_SCALE)
	public int minSum;

	public int merc;
	public int chznak;
}
