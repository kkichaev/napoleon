package com.grsoft.database;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class StoryTapeItem extends DataObject {
	@FieldOrder(order=0)
	public Date created;
	@FieldOrder(order=1)
	public String name = "";
	@FieldOrder(order=2)
	public String text = "";
	
}
