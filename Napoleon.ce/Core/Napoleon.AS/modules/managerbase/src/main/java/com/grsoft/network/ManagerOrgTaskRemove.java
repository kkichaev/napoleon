package com.grsoft.network;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.ManagerOrgTask;

public class ManagerOrgTaskRemove extends Hitching {
	
	String filter;
	
	public ManagerOrgTaskRemove(ManagerOrgTask task) {
		super(ManagerOrgTask.class, "OrgTask");
		
		filter = ":\"id\"='" + task.id + "'";
	}
	
	@Override
	public String getCommand() { return "REMOVE"; }
	
	@Override
	public String getObjectName() {
		return super.getObjectName() + filter;
	}
}
