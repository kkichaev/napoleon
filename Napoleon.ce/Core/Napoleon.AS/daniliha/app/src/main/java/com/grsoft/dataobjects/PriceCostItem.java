package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldOrder;

public class PriceCostItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	public List<PriceCostInfo> items = new ArrayList<PriceCostInfo>();
}
