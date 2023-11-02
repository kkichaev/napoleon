package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DlvMoveItem extends DataObject implements Comparable<DlvMoveItem> {
	@FieldOrder(order=0)
	public String type;

	@FieldOrder(order=1)
	public String num;
		
	@FieldOrder(order=2)
	public Date created;
	
	@FieldOrder(order=3)
	public Date date;
	
	@FieldOrder(order=4)
	@Scale(value=Consts.SUM_SCALE)
	public long sum;

	@Override public int compareTo(DlvMoveItem another) { return date.compareTo(another.date); }
}
