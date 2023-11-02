package com.grsoft.database;

import com.grsoft.dataobjects.Org;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;



public class OrgHitching extends OrgHitchingW {
	
	@Override
	public void prepareReading() {
		DbWriter.checkDBTable(dataObject);
		final String sql = "update org set hidden = 1";
		DataBaseManager.getDataBase().execSQL(sql);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Org dobj = (Org)rawObject.createDataObject(dataObject);
		dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase();
		dobj.hidden = 0;
		dbProxy.insertRecord(dobj);
	}
}
