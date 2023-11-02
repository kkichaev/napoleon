package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


@TableInfo(name="OrgBalance", keyFields="id,num")
public class OrgBalance extends DataObject {
	public String id;
	public String num;
	public Date date;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sumd;
}
