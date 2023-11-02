package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="facing", keyFields="created")
public class Facing extends CreateDocDataObject {
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
