package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="agents", keyFields="id")
public class AgentPda extends DataObject {
	public String id;
	public String name;
	public String login;
	public String password;
	public String userid;
}
