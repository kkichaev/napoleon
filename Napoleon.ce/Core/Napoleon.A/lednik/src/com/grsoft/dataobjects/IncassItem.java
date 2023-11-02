package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class IncassItem extends DataObject {
	@FieldOrder(order=0)
	public String number = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
	
	@FieldOrder(order=2)
	public Date date;
}
