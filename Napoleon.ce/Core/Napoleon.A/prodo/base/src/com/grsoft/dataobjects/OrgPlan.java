package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="orgplan", keyFields="id")
@ServerInfo(name="OrgPlan")
public class OrgPlan extends DataObject{
	public String id = "";
	public Date start;
	public Date finish;
	
	@Scale(value=Consts.WEIGHT_SCALE)
	public int value;
}
