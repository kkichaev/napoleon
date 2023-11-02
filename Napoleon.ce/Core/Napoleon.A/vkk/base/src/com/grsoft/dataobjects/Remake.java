package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="remake", keyFields="id,number")
@ServerInfo(name="Remake")
public class Remake extends DocDataObject {
	public String name = "";
	public String number = "";
	public String text = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum = 0;
}
