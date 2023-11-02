package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="ActionCost", indexes="id,idOrg,start")
public class ActCost extends DataObject {
	public String id = "";
	public String idOrg = "";
	
	public String idCost = "";
	public int priority = 0;
	
	@Scale(value = Consts.SUM_SCALE)
	public int cost;

	public Date start;
	public Date end;
}
