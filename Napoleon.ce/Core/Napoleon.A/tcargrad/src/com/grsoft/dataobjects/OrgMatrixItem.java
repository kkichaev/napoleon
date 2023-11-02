package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgMatrixItem extends MatrixItem {
	@FieldOrder(order=100)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
