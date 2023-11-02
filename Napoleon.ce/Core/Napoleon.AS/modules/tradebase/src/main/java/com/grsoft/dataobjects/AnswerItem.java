package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class AnswerItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public String answer = "";
	
	@FieldOrder(order=2)
	public int type = -1;
	
	@FieldOrder(order=3)
	public String remark = "";
	
	@FieldOrder(order=4)
	public String iditem = "";
}
