package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;

/**
 * Перед приемом очищает таблицу
 * @author 1111
 *
 */
public class RcvNewHitching extends Hitching {

	public RcvNewHitching(Class<? extends DataObject> dataObject, String objectName) {
		super(dataObject, objectName);
	}
	
	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
}
