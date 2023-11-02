package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="MaxDsc", keyFields="id")
@ServerInfo(name="MaxDiscounts")
public class MaxDiscounts extends DataObject {
	public String id = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int discount = 0;
}
