package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@SuppressLint("DefaultLocale")
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitching(){
			@Override
			public void prepareReading() {
				DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
			}
			
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				Org dobj = (Org)rawObject.createDataObject(dataObject);
				dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase();
				dbProxy.insertRecord(dobj);
			}
		};
	}
}
