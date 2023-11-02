package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RouteResultItem extends DataObject {
	@FieldOrder(order=0)
	public String name = "";
	@FieldOrder(order=1)
	public int type = -1;
	@FieldOrder(order=2)
	public Date date;
	@FieldOrder(order=3)
	public String org = "";
	@FieldOrder(order=4)
	public int idx;
	@FieldOrder(order=5)
	@Scale(Consts.SUM_SCALE)
	public int sum = 0;
	@FieldOrder(order=6)
	public List<RouteResult2Item> iitems = new ArrayList<RouteResult2Item>();
}
