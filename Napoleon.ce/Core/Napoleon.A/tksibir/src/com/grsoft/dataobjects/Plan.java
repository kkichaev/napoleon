package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="Plans", keyFields="created")
public class Plan extends CreateDocDataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
}
