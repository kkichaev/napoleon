package com.grsoft.dataobjects;

import java.io.Serializable;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RouteDocumentItem extends DataObject implements Serializable{
	private static final long serialVersionUID = 6755123356278279123L;
	
	@FieldOrder(order=0)
	public String name = "";
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
}
