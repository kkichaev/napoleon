package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class CostItemEx extends CostItem implements Comparable<CostItemEx> {
	@FieldOrder(order = 1)
	public int ctype = 0;

	@Override
	public int compareTo(CostItemEx arg0) {
		return ctype = arg0.ctype;
	}
}
