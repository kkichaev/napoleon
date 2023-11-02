package com.grsoft.database;

import com.grsoft.dataobjects.DataObjectInfo;

public class OrgHitchingEx extends OrgHitching {
	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
}
