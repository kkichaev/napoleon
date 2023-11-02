package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


@TableInfo(name="OrgBalance", keyFields="id,num")
public class OrgBalance extends DataObject {
	public String id;
	public String num;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sumd;
}
