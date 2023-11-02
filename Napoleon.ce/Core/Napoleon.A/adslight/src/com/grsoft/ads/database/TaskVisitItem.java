package com.grsoft.ads.database;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class TaskVisitItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public Date date;
}
