package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="planChanges", keyFields="id,firm,date")
public class PlanChanges extends DataObject {
	public String id;
	public String firm;
	public Date date;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
