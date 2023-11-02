package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PackItem extends DataObject {
	public static final int MAIN = 1;
	
	@FieldOrder(order=0)
	public String pack;
	
	@FieldOrder(order=1)
	public String warehouse;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int inPack;
	
	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	@FieldOrder(order=4)
	@Scale(value=1)
	public int flags;
}
