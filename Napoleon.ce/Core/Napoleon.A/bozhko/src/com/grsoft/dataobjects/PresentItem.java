package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldOrder;

public class PresentItem extends DataObject {
	@FieldOrder(order=0)
	public int col = 0;
	@FieldOrder(order=1)
	public int row = 0;
	@FieldOrder(order=2)
	public List<PresentItemPrice> ids = new ArrayList<PresentItemPrice>();
	@FieldOrder(order=3)
	public String path = "";
	@FieldOrder(order=4)
	public String desc = "";
}
