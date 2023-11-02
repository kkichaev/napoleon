package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class WSOrderLoadedItem extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	public WSOrderLoadedItem() {}
	public WSOrderLoadedItem(String id, int qty) {
		this.id = id;
		this.qty = qty;
	}
}
