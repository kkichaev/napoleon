package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class RemnantItemEx extends RemnantItem {
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int qtyWh; //на складе
	
	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int qtySh; //на полке
}
