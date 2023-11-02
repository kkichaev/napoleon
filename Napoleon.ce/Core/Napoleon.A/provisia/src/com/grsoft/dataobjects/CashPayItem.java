package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class CashPayItem extends DataObject {
	@FieldOrder(order=0)
	public String number;
	
	@FieldOrder(order=1)
	public Date date;

	@FieldOrder(order=2)
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
}
