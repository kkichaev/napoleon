package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="PrcCost", keyFields="priceid,id")
public class PriceCost extends DataObject {
	public String id;
	public String priceid;
	
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
}
