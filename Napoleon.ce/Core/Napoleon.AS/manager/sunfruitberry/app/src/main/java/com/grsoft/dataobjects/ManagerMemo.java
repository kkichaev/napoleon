package com.grsoft.dataobjects;

import java.util.Date;

public class ManagerMemo extends DataObject {
	public Date created;
	public String remark = "";
	public String userid;
	public String id;
	
	public ManagerMemo(AgentManagerMemo data) {
		created = data.created;
		userid = data.userid;
		id = data.id;
	}
}
