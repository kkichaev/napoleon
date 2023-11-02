package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="route", keyFields="id")
@ServerInfo(name="Route")
public class Route extends DataObject {
	public Date created;
	public String id = "";
	public Date start;
	public Date finish;
}
