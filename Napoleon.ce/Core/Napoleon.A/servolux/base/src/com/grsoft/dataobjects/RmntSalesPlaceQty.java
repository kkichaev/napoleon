package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RmntSalesPlaceQty extends DataObject {
	/**
	 *  од места продаж, пустой код - дл€ склада
	 */
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	@Scale(value = Consts.QTY_SCALE)
	public int qty;
}
