package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgCostModifyItem extends DataObject {
	@FieldOrder(order=0)
	public int folderID = 0;
	
	@FieldOrder(order=1)
	public int sumtype = 0;
	
	@FieldOrder(order=1)
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;
}
