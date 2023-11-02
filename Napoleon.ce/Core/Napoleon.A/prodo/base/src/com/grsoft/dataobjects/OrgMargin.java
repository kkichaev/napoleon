package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="OrgMargins", keyFields="id")
public class OrgMargin extends DataObject {
	public String id;
	
	@Scale(value=Consts.SUM_SCALE)
	public int value;
}
