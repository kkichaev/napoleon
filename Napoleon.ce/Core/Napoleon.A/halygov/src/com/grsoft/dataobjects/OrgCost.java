package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="OrgCost", keyFields="id_i,id")
public class OrgCost extends DataObject {
	public String id;
	public String id_i;
	
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
}
