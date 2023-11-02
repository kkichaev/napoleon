package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class DispatchItem extends DataObject {
	public static final int WAITING = -1;
	public static final int DONE = 1;
	public static final int REJECT = 0;
	
	@FieldOrder(order=0)
	public String number;
	@FieldOrder(order=1)
	public int state = WAITING;
	@FieldOrder(order=2)
	public String remark = "";
}
