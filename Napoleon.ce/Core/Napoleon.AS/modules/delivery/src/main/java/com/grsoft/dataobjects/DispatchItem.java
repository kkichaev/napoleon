package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.types.FieldOrder;

public class DispatchItem extends DataObject {
	public final static int DOC_NOT_INITED = 0;
	public final static int DOC_INITED = 1;
	public final static int DOC_COMPLETE = 2;
	
	@FieldOrder(order=0)
	public String itemid;
	
	@FieldOrder(order=1)
	public String number;
	
	@FieldOrder(order=2)
	public int state = 0;
	
	@FieldOrder(order=3)
	public String remark = "";
	
	@FieldOrder(order=4)
	public String type = "";
	
	@FieldOrder(order=5)
	public Date date;

	@FieldOrder(order=6)
	public String title;
}
