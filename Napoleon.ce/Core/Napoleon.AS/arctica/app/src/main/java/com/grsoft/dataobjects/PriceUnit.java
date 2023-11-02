package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceUnit extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	public String name = "";
	
	@FieldOrder(order = 2)
	@Scale(value = Consts.QTY_SCALE)
	public int inpack = 1;
	
	@Override public String toString() { return name; }
}
