package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class AgentPlanItem extends DataObject {
	@Scale(value=Consts.QTY_SCALE)
	public int value;
	public String id;
}
