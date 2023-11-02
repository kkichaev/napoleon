package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.types.FieldOrder;

public class DispatchTime extends DataObject {
	@FieldOrder(order=0)
	public Date start = new Date(0);
	@FieldOrder(order=1)
	public Date finish = new Date(0);
}
