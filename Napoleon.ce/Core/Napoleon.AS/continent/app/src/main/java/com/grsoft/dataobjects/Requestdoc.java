package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="requestdoc", keyFields="created")
public class Requestdoc extends CreateDocDataObject {
	public static final int FA_TYPE = 0;
	public static final int MOVING_TYPE = 1;
	public static final int UPD_TYPE = 2;
	
	public String number;
	public int type;
}
