package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="returnData",keyFields="unloadsum")
public class ReturnData extends DataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int unloadsum = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int returnsum = 0;
	
	@Scale(value=Consts.WEIGHT_SCALE)
	public int unloadweight = 0;
	
	@Scale(value=Consts.WEIGHT_SCALE)
	public int returnweight = 0;
}
