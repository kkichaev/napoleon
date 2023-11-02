package com.grsoft.ads.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="cagent", keyFields="id")
@ServerInfo(name="Cagents")
public class Cagent extends DataObject {
	public String id = "";
	public String name = "";
}
