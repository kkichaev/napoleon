package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public String priceType;
	
	@Scale(value=Consts.SUM_SCALE)
	public int debt;
	
	@Override
	public boolean isStopList() {
		return debt > 0;
	}
	
	public String orgid = ""; 
	
	public int checkItemGroups = 0;
}
