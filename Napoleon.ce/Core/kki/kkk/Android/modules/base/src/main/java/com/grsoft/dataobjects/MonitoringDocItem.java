package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class MonitoringDocItem extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public List<MonitoringVolumeItem> items;
	
	@FieldOrder(order=2)
	public int face;
	
	@FieldOrder(order=3)
	public int sku;

	@FieldOrder(order=4)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
}
