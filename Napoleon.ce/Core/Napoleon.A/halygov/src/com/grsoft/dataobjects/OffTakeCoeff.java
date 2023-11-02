package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="OffTakeCoef",keyFields="id")
public class OffTakeCoeff extends DataObject {
	public String id;
	public int coef;
}
