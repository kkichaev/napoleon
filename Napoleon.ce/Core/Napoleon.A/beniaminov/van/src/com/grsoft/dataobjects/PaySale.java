package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="PaySale", keyFields="created")
public class PaySale extends CreateDocDataObject {
	public String name;
	
	@Scale(value=Consts.SUM_SCALE)
	public int sum = 0;
}
