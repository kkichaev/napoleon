package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="serverinfoobject", keyFields="name")
public class ServerInfoObject extends DataObject {
	public Date time = new Date();
	public String userid = "";
	public int serverTimeZone;
	public long elapsedTime = 0;
	public String name = "serverinfoobject";
}
