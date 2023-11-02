package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="BonusDef", keyFields="id")
public class BonusDef extends DataObject  {
	public static final int PRICE = 0;
	public String id;
	public Date start;
	public Date till;
	public String iditem;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
}
