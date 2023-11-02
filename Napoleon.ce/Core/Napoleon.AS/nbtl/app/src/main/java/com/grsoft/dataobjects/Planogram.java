package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="planogram", keyFields="created")
public class Planogram extends CreateDocDataObject {
	public int approved = 0;
	public String def = "";
}
