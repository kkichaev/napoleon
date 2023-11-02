package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="VisitType")
@TableInfo(name="visittype", keyFields="id")
public class VisitType extends DataObject {
	public String id = "";
	public String name = "";
}
