package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="PriceMaxDiscount", keyFields="id")
@ServerInfo(name="PriceMaxDiscount")
public class PriceMaxDiscount extends DataObject {
	public String id = "";

	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;

}
