package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="presentinfo",keyFields="name")
public class CRCDatInfo extends DataObject {
	public String name = "";
	public long crc = -1;
}
