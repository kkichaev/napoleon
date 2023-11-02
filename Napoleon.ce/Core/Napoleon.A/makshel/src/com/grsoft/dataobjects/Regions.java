package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Regions", keyFields="id")
@ServerInfo(name="Regions")
public class Regions extends DataObject {
	public String id = "";
	public String name = "";
}
