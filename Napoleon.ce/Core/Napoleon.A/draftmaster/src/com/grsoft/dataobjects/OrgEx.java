package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int debt;
	
	@Override
	public boolean isStopList() {
		return debt > 0;
	}
}
