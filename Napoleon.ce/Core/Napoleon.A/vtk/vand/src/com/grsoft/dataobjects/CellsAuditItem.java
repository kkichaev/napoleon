package com.grsoft.dataobjects;

import com.grsoft.napoleon.CellData;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class CellsAuditItem extends DataObject {
	
	public CellsAuditItem() {}
	
	public CellsAuditItem(CellData item) {
		id = item.id;
		cell = item.cell;
		cost = item.cost;
		qty = item.rest;
		limit = item.limit;
	}

	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public int cell;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
	
	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;

	@FieldOrder(order=4)
	@Scale(value=Consts.QTY_SCALE)
	public int limit;
}
