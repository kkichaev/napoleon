package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="prezentdata", keyFields="id")
public class PrezentData extends DataObject {
	public String id = "";
	public String remark = "";
	public int color = 0xffffff;
}
