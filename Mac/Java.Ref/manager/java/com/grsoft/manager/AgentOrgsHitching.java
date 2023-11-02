package com.grsoft.manager;

import com.grsoft.database.DbWriter;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;

public class AgentOrgsHitching extends HitchOnSelect {

	public AgentOrgsHitching(String userid) {
		super(Org.class, "AgentOrgs");
		setCondition(userid);
	}

	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
}
