package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="OrderRcvd", indexes="created")
public class OrderReceived extends DataObject {
	public Date created;
	
	public String id;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
}
