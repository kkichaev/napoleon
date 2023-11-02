package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="ordersum", keyFields="userid,date", indexes="date")
@ServerInfo(name="OrderSum")
public class OrderSum extends DataObject {
	public String userid;
	public Date date;
	
	@Scale(value=Consts.SUM_SCALE)
	public int sum = 0;
}
