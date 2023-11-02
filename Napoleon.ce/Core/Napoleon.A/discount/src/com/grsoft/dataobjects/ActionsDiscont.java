package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="ActionsDisc", keyFields="id")
@ServerInfo(name="ActionsDiscount")
public class ActionsDiscont extends DataObject {
	
	public static final int DISCOUNT_TYPE = 1;
	public static final int ITEM_TYPE = 2;
	public static final int ORDER_TYPE = 3;
	
	public String id="";
	public String idWh = "";
	public String id_i = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int dsc = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int sum = 0;

	public int type = DISCOUNT_TYPE;
	
	public Date start = new Date();
	public Date finish = new Date();
}
