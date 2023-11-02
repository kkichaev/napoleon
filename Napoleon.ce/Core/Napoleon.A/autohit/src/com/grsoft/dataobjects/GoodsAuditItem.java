package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class GoodsAuditItem extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=1)
	public int shelfOur;

	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=2)
	public int shelfAll;
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=3)
	public int scuOur;
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=4)
	public int scuAll;
	
}
