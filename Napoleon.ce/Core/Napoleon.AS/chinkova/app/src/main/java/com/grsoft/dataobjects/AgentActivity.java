package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;

@ServerInfo(name="AgentActivity")
public class AgentActivity extends DataObject {
	public String login = "";
	public String phone = "";
	public String imei = "";
}
