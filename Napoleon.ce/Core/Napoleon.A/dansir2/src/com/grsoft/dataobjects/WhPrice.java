package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="whprice", keyFields="id")
public class WhPrice extends DataObject {
	public String id;
	public String name;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
