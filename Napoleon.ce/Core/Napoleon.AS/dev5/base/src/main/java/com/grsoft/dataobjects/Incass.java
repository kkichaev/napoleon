package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="Incass",keyFields="created")
public class Incass extends CreateDocDataObject {
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
}
