package com.grsoft.database;

import com.grsoft.dataobjects.Realization;
import com.grsoft.dataobjects.impl.DbObject;

public class RealizationHitching extends RcvNewHitching {

	public RealizationHitching() {
		super(DbObject.getDataType(Realization.class), "Realization");
	}

}
