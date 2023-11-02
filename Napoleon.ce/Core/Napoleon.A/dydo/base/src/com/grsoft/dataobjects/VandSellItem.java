package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class VandSellItem extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public int cell;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int chek;
	
	@FieldOrder(order=3)
	@Scale(value=Consts.QTY_SCALE)
	public int load;

	@FieldOrder(order=4)
	@Scale(value=Consts.QTY_SCALE)
	public int unload;

	@FieldOrder(order=5)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;

	@FieldOrder(order=6)
	public int cellType = CellTypes.CELL_TYPE_UNDEF;
}
