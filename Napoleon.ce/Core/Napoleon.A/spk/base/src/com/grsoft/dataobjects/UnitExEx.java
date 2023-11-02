package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class UnitExEx extends UnitEx {

	public UnitExEx(UnitItem u) {
		super(u);
	}
	
	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int coef;

}
