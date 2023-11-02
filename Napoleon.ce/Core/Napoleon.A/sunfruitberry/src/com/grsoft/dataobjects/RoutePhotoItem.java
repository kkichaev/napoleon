package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class RoutePhotoItem extends DataObject {
	@FieldOrder(order = 0)
	public String name = "";

	@FieldOrder(order = 1)
	public String smallName = "";
	
	@FieldOrder(order = 2)
	public String smallSize = ""; 
}
