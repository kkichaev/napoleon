package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="dnum", keyFields="datestr,doc", indexes="date")
@ServerInfo(name="SalesDocNumbers")
public class DNum extends DataObject {
	public Date date;
	public String doc = "";
	public int number;
	public String datestr = "";
}
