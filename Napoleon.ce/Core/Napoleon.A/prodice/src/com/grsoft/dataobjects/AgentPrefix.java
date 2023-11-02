package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="AgentPrefix",keyFields="id")
public class AgentPrefix extends DataObject {
	public String id = "";
	public String login = "";
	public String password = "";
	public String prefix = "";
}
