package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public String mid = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance = 0;

	@Scale(value=Consts.SUM_SCALE)
	public int retLimit = 0;
}
