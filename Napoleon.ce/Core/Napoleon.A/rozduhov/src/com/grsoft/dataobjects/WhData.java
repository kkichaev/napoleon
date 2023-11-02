package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class WhData extends DataObject {
	@FieldOrder(order=0)
	@Scale(value=Consts.QTY_SCALE)
	public int weight = 0;
}
