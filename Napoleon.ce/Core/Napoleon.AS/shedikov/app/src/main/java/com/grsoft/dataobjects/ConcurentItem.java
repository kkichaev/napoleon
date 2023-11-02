package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class ConcurentItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public String name = "";
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int grk = 0;
	
	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int vtr = 0;
	
	@FieldOrder(order=4)
	@Scale(value=Consts.QTY_SCALE)
	public int cmn = 0;
}
