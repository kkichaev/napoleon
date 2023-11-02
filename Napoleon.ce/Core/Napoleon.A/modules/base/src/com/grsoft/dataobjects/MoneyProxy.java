package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="MoneyProxy", keyFields = "created")
public class MoneyProxy extends CreateDocDataObject {

	@Scale(value=Consts.SUM_SCALE)
	public int sum;
}
