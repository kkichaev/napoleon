package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="TypeTP")
@TableInfo(name="typetp", keyFields="id")
public class TypeTP extends DataObject {
	public String id = "";
	public String name = "";
}
