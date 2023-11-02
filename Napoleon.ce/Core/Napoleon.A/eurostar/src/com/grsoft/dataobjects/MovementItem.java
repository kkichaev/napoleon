package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class MovementItem extends DataObject {
	public static int IN_PACK = 1;
	
	@FieldOrder(order=0)
	public String id = "";

	@FieldOrder(order=1)
	@Scale(value=1)
	public int flags;

	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;

	public boolean isInPack() {
		return (flags & IN_PACK) != 0;
	}

}
