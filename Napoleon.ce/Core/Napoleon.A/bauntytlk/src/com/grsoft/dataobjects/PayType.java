package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="paytype", keyFields="id,category")
@ServerInfo(name="PayType")
public class PayType extends DataObject {
	public String id = "";
	public String category = "";
	public String name = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int discount = 0;
}
