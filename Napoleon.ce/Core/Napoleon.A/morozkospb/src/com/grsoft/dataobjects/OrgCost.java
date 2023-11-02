package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="orgcost", keyFields="ido,id", indexes="ido")
@ServerInfo(name="OrgCost")
public class OrgCost extends DataObject {
	public String ido = "";
	public String id = "";
	public String text = "";
	
	@Scale(Consts.SUM_SCALE)
	public int cost = 0;
}
