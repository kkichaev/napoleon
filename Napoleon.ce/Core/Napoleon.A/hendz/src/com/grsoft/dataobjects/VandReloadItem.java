package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class VandReloadItem extends DataObject implements Comparable<VandReloadItem> {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public int cell;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;

	@FieldOrder(order=3)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;

	@FieldOrder(order=4)
	@Scale(value=Consts.QTY_SCALE)
	public int limit;

	@Override
	public int compareTo(VandReloadItem another) {
		return cell - another.cell;
	}
}
