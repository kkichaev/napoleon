package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.types.FieldOrder;

public class MtxStageItem extends DataObject {
	@FieldOrder(order=0)
	public String name = "";
	
	@FieldOrder(order=1)
	public Date start;
	
	@FieldOrder(order=2)
	public Date finish;
	
	@FieldOrder(order=3)
	public String remark = "";
	
}
