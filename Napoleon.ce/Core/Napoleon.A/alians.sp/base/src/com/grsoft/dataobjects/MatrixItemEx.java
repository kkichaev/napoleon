package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class MatrixItemEx extends MatrixItem {
	@FieldOrder(order=11)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
	
	@FieldOrder(order=12)
	public int spectask;
	
	@FieldOrder(order=13)
	public int pos;
}
