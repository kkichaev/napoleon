package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class MatrixItemEx extends MatrixItem {
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int face;
}
