package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDiscountItem extends DataObject {
	public int folderID = 0;
	
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;
}
