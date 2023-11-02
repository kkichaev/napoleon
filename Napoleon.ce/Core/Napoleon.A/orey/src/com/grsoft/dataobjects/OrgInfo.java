package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgInfo extends DataObject {
	public String id = "";
	public int flags;
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance;
	
	public int outDays;
}
