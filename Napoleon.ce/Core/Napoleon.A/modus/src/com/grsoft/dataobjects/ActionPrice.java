package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="actionprice", keyFields="orgid,priceid", indexes="orgid")
@ServerInfo(name="ActionPrice")
public class ActionPrice extends DataObject {
	public String orgid = "";
	public String priceid = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
	
}
