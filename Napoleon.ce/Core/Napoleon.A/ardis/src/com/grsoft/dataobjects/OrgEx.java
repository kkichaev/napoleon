package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public int delay;
	
	@Scale(value=Consts.SUM_SCALE)
	public int limit;
	
	public Date license;
	
	public String costCode;
}
