package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class AgentPlanItem extends DataObject {
	static public final int PRICE_TYPE = 0;
	static public final int FOLDER_TYPE = 1;
	
	@FieldOrder(order =0)
	@Scale(value=Consts.SUM_SCALE)
	public int valueSum;
	
//	@FieldOrder(order =1)
//	@Scale(value=Consts.QTY_SCALE)
//	public int valueQty;
	
	@FieldOrder(order =1)
	public int type;

	@FieldOrder(order =2)
	public String id = "";
}
