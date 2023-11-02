package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class AgentPlanItem extends DataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int valueSum;
	
	@Scale(value=Consts.QTY_SCALE)
	public int valueWeight;
	
	public int type;
	public String id;
}
