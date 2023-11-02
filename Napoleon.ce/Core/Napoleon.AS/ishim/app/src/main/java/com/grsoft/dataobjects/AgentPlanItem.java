package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class AgentPlanItem extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int value;

	public String getName() {
		return id.startsWith(PLAN1_TAG) ? "\"Заморозка\"" : "\"Колбасы\"";
	}
	
	public static final String PLAN1_TAG = "1\t";
	public static final String PLAN2_TAG = "2\t";
}
