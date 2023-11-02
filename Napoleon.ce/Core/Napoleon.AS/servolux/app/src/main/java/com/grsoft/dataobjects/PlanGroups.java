package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="PlanGroups")
public class PlanGroups extends DataObject {
	public String id = "";
	public String group = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int inPack;
}
