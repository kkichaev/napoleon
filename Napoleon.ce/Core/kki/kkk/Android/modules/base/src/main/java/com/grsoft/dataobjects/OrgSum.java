package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="org_sums", keyFields="id,type")
public class OrgSum extends DataObject
{
	public String id = "";
	public String type = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum = 0;
	
	public Date date;
}
