package com.grsoft.database;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;

public class PotenzialOrgRestore extends DataObjectRestore{

	public PotenzialOrgRestore() {
		super(DbObject.getDataType(Org.class), "PotenzialOrg", "created");
	}

}
