package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.AgentOrg;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Org;
import com.grsoft.network.ObjectExportListener;

public class AgentOrgHitching extends Hitching implements ObjectExportListener {

	List<AgentOrg> data = new ArrayList<AgentOrg>();

	public AgentOrgHitching(PotenzialOrgHitching orgs) {
		super(AgentOrg.class, "AgentOrg");
		String userid = AgentPrefix.get().userid;
		for (int i = 0; i < orgs.size(); i++) {
			AgentOrg ao = new AgentOrg();
			ao.id = ((Org) orgs.get(i)).id;
			ao.userid = userid;
			data.add(ao);
		}
	}

	@Override
	public int size() {	return data.size();	}

	@Override
	public DataObject get(int i) { return data.get(i); }
}
