package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgGroupItem extends DataObject {
	public String group;
	
	@Scale(value=Consts.SUM_SCALE)
	public int disc;
}
