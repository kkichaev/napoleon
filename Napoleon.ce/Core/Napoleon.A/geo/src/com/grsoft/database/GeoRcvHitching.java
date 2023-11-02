package com.grsoft.database;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class GeoRcvHitching extends Hitching{
	OrgImpl ownOrgImpl = new OrgImpl();
	
	public GeoRcvHitching() {
		super(Org.class, "GIN");
		DbWriter.checkDBTable(DbObject.getDataType(Org.class));
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Org org = (Org) rawObject.createDataObject(DbObject.getDataType(Org.class));
		org.id += "\t";
	
		ownOrgImpl.getData().id = org.id;
		org.srchName = org.name.toUpperCase();
		
		if (ownOrgImpl.read()){
			org.flags = ownOrgImpl.getData().flags;
			org.longitude = ownOrgImpl.getData().longitude;
			org.latitude = ownOrgImpl.getData().latitude;
			((OrgEx)org).geocommit = ((OrgEx)ownOrgImpl.getData()).geocommit;
			dbProxy.updateRecord(org, ownOrgImpl.getRowid());
		}else
			dbProxy.insertRecord(org);
	}
	
	@Override
	public void onEnd() {
		ownOrgImpl.close();
		super.onEnd();
	}
}
