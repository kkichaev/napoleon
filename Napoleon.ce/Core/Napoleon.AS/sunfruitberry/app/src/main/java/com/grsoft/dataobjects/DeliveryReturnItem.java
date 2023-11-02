package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DeliveryReturnItem extends DeliveryItem implements Comparable<DeliveryReturnItem>  {
	
	@FieldOrder(order=100)
	@Scale(value=Consts.QTY_SCALE)
	public int returnQty = 0;
	
	public String name = "";

	@Override
	public int compareTo(DeliveryReturnItem arg0) {
		if(returnQty > 0) {
			return arg0.returnQty == 0 ? -1 : name.compareTo(arg0.name);
		}
		return arg0.returnQty > 0 ? 1 : name.compareTo(arg0.name);
	}
}
