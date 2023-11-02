package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="dover", keyFields="id,number")
@ServerInfo(name="Dover")
public class Dover extends DocDataObject {
	public String number = "";
	@Scale(value=Consts.SUM_SCALE)
	public long sum = 0;
}
