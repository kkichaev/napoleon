package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDiscount extends DataObject {
	public static final int FOLDER_TYPE = 1; 
	public static final int PRICE_TYPE = 0; 
	
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	@Scale(value=Consts.SUM_SCALE)
	public int discount = 0;
	
	@FieldOrder(order = 2)
	public int type = 0;
}
