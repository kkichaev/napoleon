package com.grsoft.database;

import android.annotation.SuppressLint;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

@SuppressLint("DefaultLocale")
public class PotenzialOrgRcv extends Hitching {
	public PotenzialOrgRcv() {
		super(DbObject.getDataType(Org.class), "PotenzialOrg");
	}
	
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Org dobj = (Org) rawObject.createDataObject(dataObject);
		dobj.flags |= (Org.FL_USER_CREATED | Org.FL_EXPORTED);
		dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase();;
		dbProxy.insertRecord(dobj);
	}
}
