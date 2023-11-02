package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DReturnItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int inqty = 0;
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int outqty = 0;
	@FieldOrder(order=3)
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
	@FieldOrder(order=4)
	public String cause = "";
	@FieldOrder(order=5)
	public String remark = "";
}
