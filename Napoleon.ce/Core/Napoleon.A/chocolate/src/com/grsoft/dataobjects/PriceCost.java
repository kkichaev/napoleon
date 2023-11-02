package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="priceCost", keyFields="type,id")
public class PriceCost extends DataObject {
	public String id;
	
	public int type;
	
	@Scale(value=Consts.SUM_SCALE)
	public int cost;

	@Scale(value=Consts.SUM_SCALE)
	public int itemCost;
}
