package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitching(){
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				Org dobj = (Org)rawObject.createDataObject(dataObject);
				dobj.srchName = dobj.name.toUpperCase() + dobj.address.toUpperCase(); 
				dbProxy.insertRecord(dobj);
			}
		};
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new Hitching(OrgTask.class, "OrgTask"));
		return ret;
	}
}
