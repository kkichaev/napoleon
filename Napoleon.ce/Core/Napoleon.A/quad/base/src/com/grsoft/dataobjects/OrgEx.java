package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends OrgPrint {
	public String prcType;
	
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
