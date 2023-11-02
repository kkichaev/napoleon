package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="WHCost",indexes="id")
public class WHCost extends DataObject {
	public String id;
	public String idc;
	
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
}
