package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
	
	@Scale(value=Consts.SUM_SCALE)
	public int sum = 0;
	
	public int type = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	public List<BonusDefItem> items = new ArrayList<BonusDefItem>();
}
