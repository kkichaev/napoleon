package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="DIncass", keyFields="created")
public class DIncass extends DispatchDocDataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
}
