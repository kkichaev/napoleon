package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="discount", keyFields="orgSgmID,priceSgmID")
@ServerInfo(name="Discount")
public class Discount extends DataObject {
	public String orgSgmID = "";
	public String priceSgmID = "";
	
	@Scale(value=Consts.DISCOUNT_SCALE)
	public int discount = 0;
}
