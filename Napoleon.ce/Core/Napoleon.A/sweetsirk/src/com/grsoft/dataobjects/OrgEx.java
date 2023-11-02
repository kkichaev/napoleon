package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org{
	@Scale(value=Consts.SUM_SCALE)
	public int disc = 0;
	
	public int supplyer = 0;
	public int mask = 1;
}
