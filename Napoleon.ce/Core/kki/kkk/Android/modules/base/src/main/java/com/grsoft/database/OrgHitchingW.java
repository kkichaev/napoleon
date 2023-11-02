package com.grsoft.database;

import android.annotation.SuppressLint;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

@SuppressLint("DefaultLocale")
public class OrgHitchingW extends Hitching {

	public OrgHitchingW() {
		super(DbObject.getDataType(Org.class), "Org");
	}

	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Org dobj = (Org)rawObject.createDataObject(dataObject);
		dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase();
		dbProxy.insertRecord(dobj);
	}

}
