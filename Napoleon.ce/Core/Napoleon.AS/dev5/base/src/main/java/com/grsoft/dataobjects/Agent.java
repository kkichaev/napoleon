package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Agent",keyFields="id")
@ServerInfo(name="Agents")
public class Agent extends DataObject {
	public String id = "";
	public String name = "";
	public String login = "";
	public String password = "";
}
