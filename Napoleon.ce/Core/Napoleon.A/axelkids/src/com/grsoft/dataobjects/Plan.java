package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="plan", keyFields="id")
public class Plan extends DataObject {
	/***
	 * номеклатурная групп
	 ***/
	public String id = "";
	@Scale(Consts.SUM_SCALE)
	public int plan;
	
	@Scale(Consts.SUM_SCALE)
	public int fact;
}
