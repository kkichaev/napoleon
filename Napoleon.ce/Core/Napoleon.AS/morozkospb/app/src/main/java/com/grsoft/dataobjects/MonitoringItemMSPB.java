package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class MonitoringItemMSPB extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.SUM_SCALE)
	public int cost1 = 0;
	
	@FieldOrder(order=3)
	@Scale(value=Consts.SUM_SCALE)
	public int cost2 = 0;
	
	@FieldOrder(order=4)
	@FieldVersion(version=1)
	public Date date1;
	
	@FieldOrder(order=5)
	@FieldVersion(version=1)
	public Date date2;
	
	@FieldOrder(order=6)
	@FieldVersion(version=1)
	public String catID;
}
