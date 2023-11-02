package com.grsoft.napoleon;

import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitchingW;
import com.grsoft.dataobjects.DataObjectInfo;

public class UpdateDBEx extends UpdateDB{
	protected Hitching getOrgHitching() {
		return new OrgHitchingW(){
			@Override
			public void prepareReading() {
				DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
			}
		};
	}
}
