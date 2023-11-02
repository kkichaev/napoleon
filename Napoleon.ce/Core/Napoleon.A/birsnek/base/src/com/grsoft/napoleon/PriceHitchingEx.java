package com.grsoft.napoleon;

import com.grsoft.database.DbWriter;
import com.grsoft.database.PriceHitching;
import com.grsoft.dataobjects.DataObjectInfo;

public class PriceHitchingEx extends PriceHitching {
	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
}
