package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="mfr", keyFields="id")
public class Manufacturer extends DataObject {
	public String id = "";
	public String name = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int markup;
	
	@Scale(value=Consts.SUM_SCALE)
	public int expenses;
}
