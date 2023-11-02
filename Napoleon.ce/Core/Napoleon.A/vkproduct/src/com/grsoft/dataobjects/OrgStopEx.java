package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgStopEx extends DataObject {
	public String id;
	@Scale(value=Consts.SUM_SCALE)
	public int debt;
}
