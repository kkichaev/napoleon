package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="agents")
public class AgentsName extends DataObject {
	public String id;
	public String name;
	public String userid;
}
