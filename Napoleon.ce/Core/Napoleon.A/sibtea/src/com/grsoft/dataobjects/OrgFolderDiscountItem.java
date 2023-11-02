package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgFolderDiscountItem extends DataObject {
	@FieldOrder(order=0)
	public int folder;
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=1)
	public int nac;
}
