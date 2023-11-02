package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="AgentInfo", keyFields="userid")
@ServerInfo(name="UserInfo")
public class AgentInfo extends DataObject {
	public String userid = "";
	public String phone = "";
}
