package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="OffTakeCoef",keyFields="id")
public class OffTakeCoeff extends DataObject {
	public String id;
	
	@Scale(value=Consts.SUM_SCALE)
	public int coef;
}
