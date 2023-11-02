package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class Unit2Ex extends UnitEx {
	public Unit2Ex() {}
		
	public Unit2Ex(UnitItem u) {
		super(u);
	}
	
	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int coef;
	public String code;

}
