package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class IncomeItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public int folderID;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	@FieldOrder(order=3)
	public String remark;
}
