package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class OrgPriceItem extends DataObject {
	public String id = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int minQty = 0;
}
