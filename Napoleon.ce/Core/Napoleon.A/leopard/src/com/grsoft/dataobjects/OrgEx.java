package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public String stopMsg = "";

	@Scale(value=Consts.SUM_SCALE)
	public int limit;
	
	@Scale(value=1)
	public int payDelay;
}
