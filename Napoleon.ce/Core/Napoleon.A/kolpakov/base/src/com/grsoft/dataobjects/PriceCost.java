package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="PrcCost", keyFields="priceid,ido")
public class PriceCost extends DataObject {
	public String ido;
	public String priceid;
	
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
}
