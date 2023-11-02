package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDiscount extends DataObject {
	@FieldOrder(order=0)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
	
	@FieldOrder(order=1)
	public int folderID;
	
	@FieldOrder(order=2)
	public String fid;
}
