package com.grsoft.napoleon;

import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.dataobjects.DataObjectInfo;


public class UpdateDBEx extends UpdateDB {
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitching(){
			@Override
			public void prepareReading() {
				DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
			}
		};
	}
}
