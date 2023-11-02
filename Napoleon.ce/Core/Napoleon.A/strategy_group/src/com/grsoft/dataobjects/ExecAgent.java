package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="ExecAgents", keyFields="id")
public class ExecAgent extends DataObject {
	public String id;
	public String name;
	public String userid;
}
