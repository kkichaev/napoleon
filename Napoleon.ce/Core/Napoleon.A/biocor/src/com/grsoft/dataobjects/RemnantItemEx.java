package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class RemnantItemEx extends RemnantItem {
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int shelf; 
}
